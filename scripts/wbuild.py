#!/usr/bin/env python3
"""同步隔离源码快照，通过既有构建入口执行并原子回传报告。"""
import base64
import datetime
import hashlib
import io
import json
import os
from pathlib import Path, PurePosixPath
import re
import subprocess
import sys
import tarfile
import uuid

EXCLUDED = {'.git', '.gradle', '.idea', '.kotlin', 'build', 'bin', 'maven', '.wbuild', '__pycache__'}
SECRET_SUFFIXES = {'.jks', '.keystore', '.p12', '.pfx', '.gpg', '.key', '.pem'}
SECRET_NAMES = {'local.properties', 'credentials.properties', 'secrets.properties', 'key.properties'}
SENSITIVE_PROPERTY = re.compile(r'password|passwd|passphrase|pwd|token|secret|credential|privatekey|apikey|accesskey|signingkey|username', re.IGNORECASE)
TEST_SENTINEL = re.compile(r'SENTINEL_DUSKMVX_[A-Za-z0-9_-]{8,128}')


def credential_arguments_allowed(arguments):
    """在创建日志或连接前拒绝认证参数；候选测试只接受显式且完全相等的虚构哨兵。"""
    properties = []
    index = 0
    long_options = {'--project-prop': 'P', '--system-prop': 'D'}
    while index < len(arguments):
        argument = arguments[index]
        kind = None
        declaration = None
        if argument in ('-P', '-D', *long_options):
            kind = argument[1] if argument in ('-P', '-D') else long_options[argument]
            if index + 1 < len(arguments):
                index += 1
                declaration = arguments[index]
        elif argument.startswith(('-P', '-D')):
            kind, declaration = argument[1], argument[2:]
        else:
            for option, option_kind in long_options.items():
                if argument.startswith(option + '='):
                    kind, declaration = option_kind, argument[len(option) + 1:]
                    break
        if declaration is not None:
            key, _, value = declaration.partition('=')
            properties.append((kind, key, value))
        index += 1

    sentinels = [value for kind, key, value in properties if kind == 'P' and key == 'credentialSentinel']
    if sentinels and (len(sentinels) != 1 or TEST_SENTINEL.fullmatch(sentinels[0]) is None):
        return False
    sentinel = sentinels[0] if sentinels else None
    for kind, key, value in properties:
        if kind == 'P' and key == 'credentialSentinel':
            continue
        normalized = re.sub(r'[^A-Za-z0-9]', '', key)
        if SENSITIVE_PROPERTY.search(normalized) and (sentinel is None or value != sentinel):
            return False
    return True


def include_source(path):
    """仅同步可移植源码，排除本机设置、缓存和签名材料。"""
    value = PurePosixPath(path)
    return (not any(part in EXCLUDED for part in value.parts)
            and value.name.lower() not in SECRET_NAMES
            and not value.name.startswith('.env')
            and value.suffix.lower() not in SECRET_SUFFIXES)


def validate_root(value):
    """限制远端根目录，防止写入盘符根目录和父目录。"""
    from pathlib import PureWindowsPath
    path = PureWindowsPath(value)
    if not path.is_absolute() or len(path.parts) < 3 or chr(92) + chr(92) == path.anchor[:2] or ".." in path.parts:
        raise ValueError("WBUILD_ROOT 必须是隔离项目绝对目录")
    return value


def ps_string(value):
    return "'" + value.replace("'", "''") + "'"


def run(command, log, environment):
    """实时输出并留存进程的真实退出码。"""
    win = Path(environment.get('WBUILD_WIN', str(Path.home() / '.wbuild/win')))
    managed_log = Path(command[0]).resolve() == win.resolve()
    if not managed_log:
        with log.open('a', encoding='utf-8') as output:
            output.write('传输命令 ' + json.dumps(command, ensure_ascii=False) + '\n')
    with subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                          text=True, env=environment) as process:
        for line in process.stdout:
            print(line, end='', flush=True)
            # 既有远端入口已将输出写入同一日志，避免逐行重复。
            if not managed_log:
                with log.open('a', encoding='utf-8') as output:
                    output.write(line)
        code = process.wait()
    if not managed_log:
        with log.open('a', encoding='utf-8') as output:
            output.write('传输退出码=' + str(code) + '\n')
    return code


def main(arguments):
    if not credential_arguments_allowed(arguments):
        print('拒绝传输敏感认证参数；候选测试必须显式使用同一虚构哨兵。', file=sys.stderr)
        return 2
    root = Path(__file__).resolve().parent.parent
    if any(not arg.startswith("-") and re.search(r'(^|:)install[^:]*$', arg, re.IGNORECASE) for arg in arguments):
        print('安装需单独设备授权；请先调用 assemble 任务。', file=sys.stderr)
        return 2
    win = Path(os.environ.get('WBUILD_WIN', str(Path.home() / '.wbuild/win')))
    if not win.is_file():
        print('缺少构建机入口；持续集成设置 CI=true。', file=sys.stderr)
        return 2
    remote_root = validate_root(os.environ.get('WBUILD_ROOT', 'C:/dev/DuskMVX-task13')).replace(chr(92), '/')
    identifier = datetime.datetime.now().strftime('%Y%m%d-%H%M%S-') + uuid.uuid4().hex[:8]
    snapshot = remote_root + '/snapshots/' + identifier
    log = Path(os.environ.get('WBUILD_LOG', str(root / 'build/wbuild' / (identifier + '.log'))))
    log.parent.mkdir(parents=True, exist_ok=True)
    environment = dict(os.environ, WBUILD_LOG=str(log))
    host = os.environ.get('WBUILD_HOST', 'winbuild')
    if host != "winbuild" and "WBUILD_WIN" not in os.environ:
        raise ValueError("自定义传输主机必须提供对应的 WBUILD_WIN 入口")
    with log.open('a', encoding='utf-8') as output:
        output.write('开始 ' + identifier + '\n远端快照 ' + snapshot + '\n')
    displayed = [re.sub(r"(?i)(password|token|secret)([^=]*=).*", r"\1\2[已隐藏]", value) for value in arguments]
    with log.open("a", encoding="utf-8") as output:
        output.write("命令 bash gradlew " + json.dumps(displayed, ensure_ascii=False) + "\n")
    print('远端快照：' + snapshot, flush=True)
    create = '$ErrorActionPreference="Stop"; $p=' + ps_string(snapshot) + '; Write-Output ([IO.Path]::GetFullPath($p)); if(Test-Path -LiteralPath $p){throw "快照已存在"}; New-Item -ItemType Directory -Path $p -Force | Out-Null'
    artifact = root / 'build/wbuild' / identifier
    artifact.mkdir(parents=True, exist_ok=True)
    code = 1
    try:
        code = run([str(win), create], log, environment)
        if code:
            return code
        # 源码归档属于验收证据，保留在构建报告目录，不自动永久删除。
        archive = artifact / 'source.tgz'
        source_hashes = {}
        with tarfile.open(archive, 'w:gz') as output:
            for path in sorted(root.rglob('*')):
                relative = path.relative_to(root).as_posix()
                if path.is_file() and not path.is_symlink() and path.resolve() != log.resolve() and include_source(relative):
                    contents = path.read_bytes()
                    member = output.gettarinfo(str(path), arcname=relative)
                    member.size = len(contents)
                    output.addfile(member, io.BytesIO(contents))
                    source_hashes[relative] = hashlib.sha256(contents).hexdigest()
        archive_hash = hashlib.sha256(archive.read_bytes()).hexdigest()
        (artifact / "source-manifest.json").write_text(json.dumps({"files": source_hashes, "archiveSha256": archive_hash}, indent=2) + "\n", encoding="utf-8")
        remote_archive = snapshot + '/source.tgz'
        code = run(['scp', '-q', str(archive), host + ':' + remote_archive], log, environment)
        if code:
            return code
        encoded_args = base64.b64encode(json.dumps(arguments, ensure_ascii=True).encode()).decode()
        command = '& ' + ps_string(snapshot + '/scripts/wbuild-run.ps1') + ' -Snapshot ' + ps_string(snapshot) + ' -ArgumentsBase64 ' + ps_string(encoded_args)
        execute = '$ErrorActionPreference="Stop"; $p=' + ps_string(snapshot) + '; Write-Output ([IO.Path]::GetFullPath($p)); Set-Location -LiteralPath $p; if((Get-FileHash -LiteralPath source.tgz -Algorithm SHA256).Hash -ne ' + ps_string(archive_hash) + '){throw "源码归档校验失败"}; tar -xzf source.tgz; if($LASTEXITCODE -ne 0){exit $LASTEXITCODE}; ' + command
        code = run([str(win), execute], log, environment)
        part = artifact / 'reports.tgz.part'
        copied = run(['scp', '-q', host + ':' + snapshot + '/reports.tgz', str(part)], log, environment)
        if copied == 0:
            with tarfile.open(part, 'r:gz') as source:
                for member in source.getmembers():
                    target = (artifact / member.name).resolve()
                    if (not target.is_relative_to(artifact.resolve())
                            or not (member.isfile() or member.isdir())
                            or not member.name.startswith(('dawn/build/', 'app/build/', 'auto-service-ksp/build/'))):
                        code = code or 1
                        raise ValueError('报告包含不安全路径或文件类型')
                source.extractall(artifact)
            part.replace(artifact / 'reports.tgz')
        elif code == 0:
            code = copied
        return code
    except Exception:
        code = code or 1
        raise
    finally:
        with log.open('a', encoding='utf-8') as output:
            output.write('结束 ' + datetime.datetime.now().isoformat() + ' 退出码=' + str(code) + '\n报告=' + str(artifact) + '\n')
        print('退出码=' + str(code) + ' 报告=' + str(artifact), flush=True)


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))

