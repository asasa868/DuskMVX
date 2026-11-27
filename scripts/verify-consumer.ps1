param(
    [Parameter(Mandatory = $true)][string]$SourceRoot,
    [Parameter(Mandatory = $true)][string]$CandidateRepository,
    [Parameter(Mandatory = $true)][string]$WorkRoot,
    [string]$CandidateVersion = '',
    [string]$SdkRoot = ''
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$exitCode = 1
$transcriptStarted = $false
$locationPushed = $false

try {
    $source = (Resolve-Path -LiteralPath $SourceRoot).Path
    $candidate = (Resolve-Path -LiteralPath $CandidateRepository).Path
    $work = [System.IO.Path]::GetFullPath($WorkRoot)
    if ([string]::IsNullOrWhiteSpace($CandidateVersion)) {
        $versionLine = Get-Content -LiteralPath (Join-Path $source 'gradle.properties') | Where-Object { $_ -match '^PUBLISH_VERSION=' } | Select-Object -First 1
        if ($null -eq $versionLine) { throw '未配置候选发布版本' }
        $CandidateVersion = ($versionLine -split '=', 2)[1].Trim()
    }
    Write-Host "消费者源码根：$source"
    Write-Host "候选仓库绝对路径：$candidate"
    Write-Host "消费者独立目录：$work"
    if (-not [System.IO.Path]::IsPathRooted($WorkRoot)) { throw '消费者目录必须是绝对路径' }
    if (Test-Path -LiteralPath $work) { throw '消费者目录已经存在，拒绝覆盖已有状态，请提供新的隔离目录' }
    $fixture = Join-Path $source 'scripts/consumer-fixture'
    if (-not (Test-Path -LiteralPath $fixture -PathType Container)) { throw '消费者夹具不存在' }
    $aar = Join-Path $candidate ("io/github/asasa868/Dusk/$CandidateVersion/Dusk-$CandidateVersion.aar")
    if (-not (Test-Path -LiteralPath $aar -PathType Leaf)) { throw '候选归档不存在' }
    if ([string]::IsNullOrWhiteSpace($SdkRoot)) { $SdkRoot = $env:ANDROID_HOME }
    if ([string]::IsNullOrWhiteSpace($SdkRoot)) { $SdkRoot = $env:ANDROID_SDK_ROOT }
    if ([string]::IsNullOrWhiteSpace($SdkRoot)) { throw '需要明确的安卓工具目录环境或 SdkRoot 参数' }
    $sdk = (Resolve-Path -LiteralPath $SdkRoot).Path
    Write-Host "安卓工具绝对路径：$sdk"

    New-Item -ItemType Directory -Path $work | Out-Null
    Start-Transcript -LiteralPath (Join-Path $work 'consumer-validation.log') | Out-Null
    $transcriptStarted = $true
    Get-ChildItem -LiteralPath $fixture -Force | ForEach-Object {
        Copy-Item -LiteralPath $_.FullName -Destination $work -Recurse
    }
    Copy-Item -LiteralPath (Join-Path $source 'gradlew.bat') -Destination (Join-Path $work 'gradlew.bat')
    $wrapper = Join-Path $work 'gradle/wrapper'
    New-Item -ItemType Directory -Path $wrapper -Force | Out-Null
    Copy-Item -LiteralPath (Join-Path $source 'gradle/wrapper/gradle-wrapper.jar') -Destination $wrapper
    Copy-Item -LiteralPath (Join-Path $source 'gradle/wrapper/gradle-wrapper.properties') -Destination $wrapper
    # 只写明示的工具目录，不读取或复制开发机私密配置；盘符冒号按属性文件规则转义。
    $sdkProperty = 'sdk.dir=' + $sdk.Replace('\', '/').Replace(':', '\:')
    [System.IO.File]::WriteAllText((Join-Path $work 'local.properties'), $sdkProperty, [System.Text.UTF8Encoding]::new($false))
    $arguments = @(
        ('-PcandidateRepository=' + $candidate),
        ('-PcandidateAar=' + $aar),
        ('-PcandidateVersion=' + $CandidateVersion),
        'assembleDebug', 'testDebugUnitTest', 'lintDebug', 'verifyConsumerDependencies',
        '--stacktrace', '--info', '--no-daemon', '--refresh-dependencies'
    )
    Write-Host ('消费者任务参数：' + ($arguments | ConvertTo-Json -Compress))
    Write-Host ('开始时间：' + [DateTime]::UtcNow.ToString('o'))
    Push-Location -LiteralPath $work
    $locationPushed = $true
    & (Join-Path $work 'gradlew.bat') @arguments
    $exitCode = $LASTEXITCODE
    Write-Host "消费者退出码：$exitCode"
    Write-Host ('结束时间：' + [DateTime]::UtcNow.ToString('o'))
    if ($exitCode -ne 0) { throw "消费者验证失败，退出码 $exitCode" }
    $xmlReports = @(Get-ChildItem -LiteralPath (Join-Path $work 'build/test-results/testDebugUnitTest') -Filter 'TEST-*.xml' -File)
    if ($xmlReports.Count -eq 0) { $exitCode = 1; throw '消费者没有生成实际测试报告' }
    $tests = 0
    foreach ($file in $xmlReports) {
        [xml]$report = Get-Content -LiteralPath $file.FullName -Raw
        $tests += [int]$report.testsuite.tests
        if ([int]$report.testsuite.failures -gt 0 -or [int]$report.testsuite.errors -gt 0 -or [int]$report.testsuite.skipped -gt 0) {
            $exitCode = 1
            throw '消费者测试存在失败、错误或跳过'
        }
    }
    if ($tests -lt 2) { $exitCode = 1; throw '消费者测试执行数量不足' }
    Write-Host "消费者实际执行用例：$tests"
    Write-Host ('消费者依赖报告：' + (Join-Path $work 'build/reports/consumer-dependencies.txt'))
    $exitCode = 0
} catch {
    if ($exitCode -eq 0) { $exitCode = 1 }
    Write-Error -ErrorAction Continue $_
} finally {
    if ($locationPushed) { Pop-Location }
    if ($transcriptStarted) { Stop-Transcript | Out-Null }
}
exit $exitCode

