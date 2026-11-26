"""校验候选提交、产物摘要与发布渠道响应，不创建标签或发布。"""

import argparse
import hashlib
import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

REQUIRED_CHECKS = {"build", "tests", "lint", "candidate", "consumer"}
REQUIRED_TEST_MODULES = ("dawn", "auto-service-ksp", "dawn-device")
COMMIT_PATTERN = re.compile(r"^[0-9a-f]{40}$")
VERSION_PATTERN = re.compile(r"^[0-9]+\.[0-9]+\.[0-9]+(?:[-+][0-9A-Za-z][0-9A-Za-z.-]*)?$")


class GateError(ValueError):
    """候选或渠道证据不满足发布门禁。"""


def check_commit(commit):
    if not COMMIT_PATTERN.fullmatch(commit):
        raise GateError("必须使用完整提交标识")


def file_hashes(root, manifest):
    root = Path(root).resolve()
    manifest = Path(manifest).resolve()
    hashes = {}
    for path in sorted(root.rglob("*")):
        if path.is_symlink():
            raise GateError("候选包不得包含符号链接")
        if path.is_file() and path.resolve() != manifest:
            hashes[path.relative_to(root).as_posix()] = hashlib.sha256(path.read_bytes()).hexdigest()
    if not hashes:
        raise GateError("候选包为空")
    for suffix in (".pom", ".aar", ".module", "-sources.jar"):
        if not any(name.endswith(suffix) for name in hashes):
            raise GateError("候选缺少必要产物：" + suffix)
    return hashes


def count_tests(root):
    total = 0
    for path in Path(root).rglob("TEST-*.xml"):
        try:
            suite = ET.parse(path).getroot()
            if suite.tag != "testsuite":
                raise GateError("测试报告根节点必须为测试套件")
            tests = int(suite.attrib.get("tests", "0"))
            failures = int(suite.attrib.get("failures", "0")) + int(suite.attrib.get("errors", "0"))
            skipped = int(suite.attrib.get("skipped", "0"))
        except (ET.ParseError, ValueError) as error:
            raise GateError("测试报告格式错误") from error
        if failures or skipped or tests <= 0 or tests < skipped:
            raise GateError("测试报告存在失败或跳过")
        total += tests - skipped
    if total == 0:
        raise GateError("没有实际执行通过的测试")
    return total


def count_module_tests(root):
    """分别核验必要模块，防止其他模块的报告掩盖缺失或未执行的测试。"""
    return {module: count_tests(Path(root) / "reports" / module / "test-results")
            for module in REQUIRED_TEST_MODULES}


def record_candidate(root, output, commit, checks):
    check_commit(commit)
    if set(checks) != REQUIRED_CHECKS or any(value != "passed" for value in checks.values()):
        raise GateError("构建、测试、静态检查、产物和消费者检查必须全部通过")
    manifest = {"schema": 1, "commit": commit, "checks": dict(checks),
                "tests": count_tests(root), "consumerTests": count_consumer_tests(root),
                "moduleTests": count_module_tests(root),
                "sha256": file_hashes(root, output)}
    Path(output).write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    return manifest


def verify_candidate(root, manifest_path, commit):
    check_commit(commit)
    try:
        manifest = json.loads(Path(manifest_path).read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        raise GateError("候选清单不可读取") from error
    if not isinstance(manifest, dict) or manifest.get("schema") != 1 or manifest.get("commit") != commit:
        raise GateError("候选提交与标签提交不一致")
    checks = manifest.get("checks")
    if not isinstance(checks, dict) or set(checks) != REQUIRED_CHECKS or any(v != "passed" for v in checks.values()):
        raise GateError("候选存在未通过门禁")
    if manifest.get("sha256") != file_hashes(root, manifest_path):
        raise GateError("候选产物摘要不匹配或文件集合变化")
    if manifest.get("tests") != count_tests(root):
        raise GateError("测试报告与候选清单不一致")
    if manifest.get("consumerTests") != count_consumer_tests(root):
        raise GateError("消费者报告与候选清单不一致")
    if manifest.get("moduleTests") != count_module_tests(root):
        raise GateError("必要模块报告与候选清单不一致")
    return manifest


def count_consumer_tests(root):
    consumer = Path(root) / "reports" / "consumer"
    count = count_tests(consumer / "test-results")
    if count < 2:
        raise GateError("消费者实际用例不足")
    report = consumer / "consumer-dependencies.txt"
    if not report.is_file() or not report.read_text(encoding="utf-8").strip():
        raise GateError("消费者依赖验证报告缺失")
    return count


def validate_release_tag(tag, version):
    """只接受候选配置中的版本及其带小写版本前缀的标签，非法值抛出门禁异常。"""
    if not VERSION_PATTERN.fullmatch(version) or tag not in {version, "v" + version}:
        raise GateError("发布标签必须匹配候选配置版本，可使用小写版本前缀")
    return tag


def validate_release_policy(policy):
    """核验接口返回的人工审核与完整标签保护，缺失或存在人工绕过时拒绝发布。"""
    if not isinstance(policy, dict):
        raise GateError("发布策略格式错误")
    environment = policy.get("environment")
    if not isinstance(environment, dict):
        raise GateError("发布环境策略缺失")
    protections = environment.get("protection_rules", [])
    if not isinstance(protections, list) or not any(
            isinstance(rule, dict) and rule.get("type") == "required_reviewers"
            and isinstance(rule.get("reviewers"), list) and rule["reviewers"]
            for rule in protections):
        raise GateError("发布环境必须配置人工审核")
    rulesets = policy.get("rulesets")
    if not isinstance(rulesets, list):
        raise GateError("标签创建保护策略缺失")
    for ruleset in rulesets:
        if not isinstance(ruleset, dict) or ruleset.get("target") != "tag" or ruleset.get("enforcement") != "active":
            continue
        conditions = ruleset.get("conditions")
        names = conditions.get("ref_name") if isinstance(conditions, dict) else None
        if not isinstance(names, dict) or not isinstance(names.get("include"), list):
            continue
        if "~ALL" not in names["include"] or names.get("exclude") != []:
            continue
        bypasses = ruleset.get("bypass_actors")
        if not isinstance(bypasses, list) or len(bypasses) > 1 or any(
                not isinstance(actor, dict) or actor.get("actor_type") != "Integration"
                or type(actor.get("actor_id")) is not int or actor["actor_id"] <= 0
                or actor.get("bypass_mode") != "always" for actor in bypasses):
            continue
        rules = ruleset.get("rules")
        if isinstance(rules, list) and any(isinstance(rule, dict) and rule.get("type") == "creation" for rule in rules):
            return
    raise GateError("必须配置覆盖全部标签且无排除项的有效创建限制，仅可允许单一自动化应用绕过")


def read_release_policy(path):
    """读取本次接口查询保存的策略文件，不读取认证信息。"""
    try:
        return json.loads(Path(path).read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as error:
        raise GateError("发布策略不可读取") from error


def authorize_tag_creation(root, manifest, commit, policy, tag, version):
    """候选、提交、远端策略及版本均符合约束时返回可创建标签，否则抛出门禁异常。"""
    verify_candidate(root, manifest, commit)
    validate_release_policy(policy)
    return validate_release_tag(tag, version)


def validate_jitpack(status_code, response_body, commit, tag, module):
    check_commit(commit)
    if not 200 <= status_code < 300:
        raise GateError("发布渠道请求失败，状态码：" + str(status_code))
    try:
        result = json.loads(response_body)
    except (json.JSONDecodeError, UnicodeDecodeError, TypeError) as error:
        raise GateError("发布渠道未返回合法数据") from error
    if not isinstance(result, dict) or result.get("status") != "ok":
        raise GateError("发布渠道构建未成功")
    if result.get("commit") != commit or result.get("version") != tag:
        raise GateError("发布渠道提交或版本与候选不一致")
    modules = result.get("modules")
    if not isinstance(modules, list) or module not in modules:
        raise GateError("发布渠道未声明预期模块")
    return result


def fetch_jitpack(url, commit, tag, module):
    if not url.startswith("https://jitpack.io/api/builds/"):
        raise GateError("只允许查询指定发布渠道")
    try:
        request = Request(url, headers={"Accept": "application/json", "User-Agent": "DuskMVX-release-gate"})
        with urlopen(request, timeout=60) as response:
            return validate_jitpack(response.status, response.read(), commit, tag, module)
    except (HTTPError, URLError, TimeoutError, OSError) as error:
        raise GateError("发布渠道网络请求失败") from error


def main(argv=None):
    parser = argparse.ArgumentParser(description="候选发布校验")
    commands = parser.add_subparsers(dest="command", required=True)
    record = commands.add_parser("record")
    record.add_argument("--root", required=True)
    record.add_argument("--manifest", required=True)
    record.add_argument("--commit", required=True)
    record.add_argument("--check", action="append", default=[])
    verify = commands.add_parser("verify")
    verify.add_argument("--root", required=True)
    verify.add_argument("--manifest", required=True)
    verify.add_argument("--commit", required=True)
    channel = commands.add_parser("jitpack")
    channel.add_argument("--url", required=True)
    channel.add_argument("--commit", required=True)
    channel.add_argument("--tag", required=True)
    channel.add_argument("--module", required=True)
    tag = commands.add_parser("tag")
    tag.add_argument("--tag", required=True)
    tag.add_argument("--version", required=True)
    policy = commands.add_parser("policy")
    policy.add_argument("--path", required=True)
    authorize = commands.add_parser("authorize-tag")
    authorize.add_argument("--root", required=True)
    authorize.add_argument("--manifest", required=True)
    authorize.add_argument("--commit", required=True)
    authorize.add_argument("--policy", required=True)
    authorize.add_argument("--tag", required=True)
    authorize.add_argument("--version", required=True)
    args = parser.parse_args(argv)
    try:
        if args.command == "record":
            checks = {}
            for item in args.check:
                name, separator, value = item.partition("=")
                if not separator or name in checks:
                    raise GateError("检查参数重复或格式错误")
                checks[name] = value
            record_candidate(args.root, args.manifest, args.commit, checks)
        elif args.command == "verify":
            verify_candidate(args.root, args.manifest, args.commit)
        elif args.command == "tag":
            validate_release_tag(args.tag, args.version)
        elif args.command == "policy":
            validate_release_policy(read_release_policy(args.path))
        elif args.command == "authorize-tag":
            authorize_tag_creation(args.root, args.manifest, args.commit,
                                   read_release_policy(args.policy), args.tag, args.version)
        else:
            fetch_jitpack(args.url, args.commit, args.tag, args.module)
        print("发布门禁通过")
    except GateError as error:
        print(str(error), file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())

