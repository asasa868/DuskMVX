param([Parameter(Mandatory=$true)][string]$Snapshot, [Parameter(Mandatory=$true)][string]$ArgumentsBase64)
$ErrorActionPreference = 'Stop'
$resolved = [IO.Path]::GetFullPath($Snapshot)
Write-Output "实际构建目录：$resolved"
if ($resolved.Replace([char]92,[char]47) -notmatch '^[A-Za-z]:/.+/snapshots/[0-9]{8}-[0-9]{6}-[a-f0-9]{8}$') { throw '拒绝非隔离快照目录' }
Set-Location -LiteralPath $resolved
$decodedArguments = ConvertFrom-Json ([Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($ArgumentsBase64)))
# 旧版终端将顶层数组作为一个管道对象输出，外层收集会把整组参数嵌套起来。
if ($null -eq $decodedArguments) {
    $arguments = @()
} elseif ($decodedArguments -is [array]) {
    $arguments = $decodedArguments
} else {
    $arguments = @($decodedArguments)
}
foreach ($argument in $arguments) {
    if ($argument -isnot [string]) { throw '构建参数必须是字符串数组' }
}
if (-not $env:ANDROID_HOME) { throw '构建机必须配置安卓开发工具目录' }
# 仅配置远端必需的开发工具路径，不同步开发机私密属性；盘符冒号按属性文件规则转义。
'sdk.dir=' + $env:ANDROID_HOME.Replace([char]92,[char]47).Replace(':', '\:') | Set-Content -LiteralPath local.properties -Encoding Ascii
$env:CI = 'true'

function ConvertTo-JavaCommandArgument([string]$Value) {
    # 成对引号保证命令处理器始终包裹元字符，反斜杠按启动器规则转义。
    if ($Value.IndexOf([char]0) -ge 0 -or $Value.Contains([char]13) -or $Value.Contains([char]10)) {
        throw '构建参数不能包含空字符或换行'
    }
    $quoted = [regex]::Replace($Value, '(\\*)"', '$1$1""')
    $quoted = [regex]::Replace($quoted, '(\\+)$', '$1$1')
    return '"' + $quoted + '"'
}

# 传统终端会吞嵌入引号；只在命令文件中展开一次环境变量，避免重新解释参数。
$invocation = [Diagnostics.ProcessStartInfo]::new()
$invocation.FileName = $env:ComSpec
$invocation.UseShellExecute = $false
$invocation.WorkingDirectory = $resolved
$invocation.Arguments = '/d /v:off /c wbuild-invoke.cmd'
$invocation.EnvironmentVariables['DUSKMVX_WRAPPER'] = (Join-Path $resolved 'gradlew.bat')
$references = @()
for ($index = 0; $index -lt $arguments.Count; $index++) {
    $name = 'DUSKMVX_ARGUMENT_' + $index
    $invocation.EnvironmentVariables[$name] = ConvertTo-JavaCommandArgument ([string]$arguments[$index])
    $references += '%' + $name + '%'
}
$commandFile = @('@echo off', 'setlocal DisableDelayedExpansion', ('"%DUSKMVX_WRAPPER%" ' + ($references -join ' '))) -join [Environment]::NewLine
[IO.File]::WriteAllText((Join-Path $resolved 'wbuild-invoke.cmd'), $commandFile + [Environment]::NewLine, [Text.Encoding]::ASCII)
Write-Output ('开始时间：' + [DateTime]::UtcNow.ToString('o'))
$taskProcess = [Diagnostics.Process]::new()
$taskProcess.StartInfo = $invocation
try {
    if (-not $taskProcess.Start()) { throw '原生构建入口未启动' }
    $taskProcess.WaitForExit()
    $code = $taskProcess.ExitCode
} finally {
    $taskProcess.Dispose()
}
Write-Output ('结束时间：' + [DateTime]::UtcNow.ToString('o') + ' 退出码=' + $code)
# 单目录同样保留数组类型，避免原生命令展开字符串时逐字符传参。
$paths = @(@('dawn/build/reports','dawn/build/test-results','dawn/build/outputs','dawn/build/publications','dawn/build/candidate-repository','app/build/reports','app/build/test-results','app/build/outputs','auto-service-ksp/build/reports','auto-service-ksp/build/test-results','auto-service-ksp/build/ksp-fixtures') | Where-Object { Test-Path -LiteralPath $_ })
if ($paths.Count -gt 0) {
    & tar -czf reports.tgz @paths
    if ($LASTEXITCODE -ne 0 -and $code -eq 0) { $code = $LASTEXITCODE }
} else {
    & tar -czf reports.tgz --files-from NUL
    if ($LASTEXITCODE -ne 0 -and $code -eq 0) { $code = $LASTEXITCODE }
}
exit $code

