"""构建转发边界的离线回归，不调用构建机或执行真实构建。"""
import base64
import contextlib
import importlib.util
import io
import json
import os
from pathlib import Path
import re
import subprocess
import tarfile
import unittest
from unittest import mock
import uuid

ROOT = Path(__file__).resolve().parents[2]
SPEC = importlib.util.spec_from_file_location("duskmvx_wbuild", ROOT / "scripts" / "wbuild.py")
wbuild = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(wbuild)


class EvidenceDirectory:
    """保留离线夹具供验收检查，避免测试清理永久删除文件。"""

    def __init__(self, prefix="fixture-"):
        path = ROOT / "build/reports/wbuild-script-fixtures" / (prefix + uuid.uuid4().hex)
        path.mkdir(parents=True)
        self.name = str(path)

    def __enter__(self):
        return self.name

    def __exit__(self, *_):
        return False


class SourceBoundaryTest(unittest.TestCase):
    def test_source_and_wrapper_files_are_included(self):
        for name in ("app/src/main/App.kt", "gradle/wrapper/gradle-wrapper.jar", "gradlew.local", "scripts/wbuild-run.ps1"):
            with self.subTest(name=name):
                self.assertTrue(wbuild.include_source(name))

    def test_caches_local_settings_and_signing_files_are_excluded(self):
        names = (".git/config", ".idea/misc.xml", "app/build/classes/A.class", "auto-service-ksp/bin/A.class",
                 ".gradle/cache.bin", "local.properties", ".env.production", "signing/release.jks",
                 "signing/release.KEY", "signing/secring.gpg", "nested/.wbuild/config")
        for name in names:
            with self.subTest(name=name):
                self.assertFalse(wbuild.include_source(name))

    def test_common_secret_material_is_not_synchronized(self):
        for name in ("signing/private.pem", "credentials.properties", "Credentials.properties", "secrets.properties", "key.properties"):
            with self.subTest(name=name):
                self.assertFalse(wbuild.include_source(name))

    def test_remote_root_rejects_drive_root_relative_unc_and_parent_paths(self):
        for value in ("C:/", "C:/dev", "relative/project", "C:/dev/../other", "//server/share/project"):
            with self.subTest(value=value):
                with self.assertRaises(ValueError):
                    wbuild.validate_root(value)

    def test_remote_root_and_powershell_quoting_preserve_spaces(self):
        value = "C:/dev/Dusk workspace"
        self.assertEqual(value, wbuild.validate_root(value))
        self.assertEqual("'C:/dev/O''Brien workspace'", wbuild.ps_string("C:/dev/O'Brien workspace"))


class ForwardingTest(unittest.TestCase):
    def setUp(self):
        self.directory = EvidenceDirectory(prefix="forwarding ")
        self.root = Path(self.directory.name) / "project with spaces"
        (self.root / "scripts").mkdir(parents=True)
        (self.root / "app/src/main").mkdir(parents=True)
        (self.root / "app/src/main/App.kt").write_text("源码", encoding="utf-8")
        (self.root / "local.properties").write_text("虚构本机值", encoding="utf-8")
        (self.root / "signing").mkdir()
        (self.root / "signing/release.jks").write_bytes(b"fake signing file")
        self.win = Path(self.directory.name) / "win entry"
        self.win.write_text("离线占位", encoding="utf-8")
        self.environment = mock.patch.dict(os.environ, {
            "WBUILD_WIN": str(self.win), "WBUILD_ROOT": "C:/dev/Dusk workspace",
            "WBUILD_HOST": "isolated-host", "WBUILD_LOG": str(self.root / "logs/session.log")
        })
        self.environment.start()
        self.addCleanup(self.environment.stop)
        patch = mock.patch.object(wbuild, "__file__", str(self.root / "scripts/wbuild.py"))
        patch.start()
        self.addCleanup(patch.stop)
        self.calls = []
        self.uploads = []
        self.remote_code = 0
        self.copy_code = 0
        self.create_code = 0
        self.archive_member = "dawn/build/reports/result.txt"
        self.archive_type = tarfile.REGTYPE

    def fake_run(self, command, log, environment):
        self.calls.append(command)
        self.assertEqual(str(log), environment["WBUILD_LOG"])
        if command[0] == str(self.win):
            if "New-Item" in command[1]:
                return self.create_code
            return self.remote_code
        self.assertEqual("scp", command[0])
        if command[2].endswith("source.tgz"):
            with tarfile.open(command[2], "r:gz") as archive:
                self.uploads.append(archive.getnames())
            return 0
        if self.copy_code:
            return self.copy_code
        with tarfile.open(command[-1], "w:gz") as archive:
            payload = b"result"
            member = tarfile.TarInfo(self.archive_member)
            member.type = self.archive_type
            member.size = len(payload) if member.isfile() else 0
            archive.addfile(member, io.BytesIO(payload) if member.isfile() else None)
        return 0

    def invoke(self, arguments):
        with mock.patch.object(wbuild, "run", side_effect=self.fake_run), contextlib.redirect_stdout(io.StringIO()):
            return wbuild.main(arguments)

    def test_credential_properties_are_rejected_before_network_or_log_creation(self):
        value = "FICTIONAL_REJECTED_VALUE_73a5e1"
        declarations = (
            ["-PpublishPassword=" + value], ["-Dauth.token=" + value],
            ["-PpublishUsername=" + value], ["-Papi_key=" + value],
            ["-Dsigning.passphrase=" + value], ["-Dorg.gradle.project.privateKey=" + value],
            ["-P", "publishPassword=" + value], ["-D", "auth.secret=" + value],
            ["--project-prop", "publishUsername=" + value],
            ["--system-prop=access_token=" + value],
            ["--project-prop=repository.credentials.password=" + value],
        )
        for options in declarations:
            with self.subTest(options=options), contextlib.redirect_stderr(io.StringIO()) as errors:
                self.assertEqual(2, self.invoke(["help", *options]))
                self.assertNotIn(value, errors.getvalue())
                self.assertNotIn(base64.b64encode(value.encode()).decode(), errors.getvalue())
        self.assertEqual([], self.calls)
        self.assertEqual([], self.uploads)
        self.assertFalse((self.root / "logs").exists())
        self.assertFalse((self.root / "build").exists())

    def test_rejection_preserves_existing_log_and_never_records_rejected_value(self):
        value = "FICTIONAL_REJECTED_LOG_VALUE_349f6a"
        log = self.root / "logs/session.log"
        log.parent.mkdir(parents=True)
        log.write_text("既有安全日志\n", encoding="utf-8")
        with contextlib.redirect_stderr(io.StringIO()) as errors:
            self.assertEqual(2, self.invoke(["help", "-PpublishPassword=" + value]))
        self.assertEqual("既有安全日志\n", log.read_text(encoding="utf-8"))
        self.assertNotIn(value, errors.getvalue())
        self.assertEqual([], self.calls)

    def test_credentials_require_one_explicit_well_formed_and_exactly_matching_sentinel(self):
        sentinel = "SENTINEL_DUSKMVX_TEST_26ab42cd"
        scenarios = (
            ["-PpublishPassword=" + sentinel],
            ["-PcredentialSentinel=" + sentinel, "-PpublishPassword=" + sentinel + "_different"],
            ["-PcredentialSentinel=" + sentinel, "-PpublishPassword="],
            ["-PcredentialSentinel=" + sentinel, "-PcredentialSentinel=" + sentinel],
            ["-DcredentialSentinel=" + sentinel, "-PpublishPassword=" + sentinel],
            ["-PcredentialSentinel=FICTIONAL_BUT_NOT_RESERVED", "-PpublishPassword=FICTIONAL_BUT_NOT_RESERVED"],
            ["-PcredentialSentinel="],
        )
        for options in scenarios:
            with self.subTest(options=options), contextlib.redirect_stderr(io.StringIO()):
                self.assertEqual(2, self.invoke(["help", *options]))
        self.assertEqual([], self.calls)
        self.assertFalse((self.root / "logs").exists())

    def test_explicit_fictional_sentinel_allows_matching_credential_test_values(self):
        sentinel = "SENTINEL_DUSKMVX_TEST_26ab42cd"
        arguments = [":dawn:verifyCandidate", "-PcredentialSentinel=" + sentinel,
                     "-PpublishUsername=" + sentinel, "-PpublishPassword=" + sentinel,
                     "-D", "auth.token=" + sentinel, "--project-prop", "repository.secret=" + sentinel]

        self.assertEqual(0, self.invoke(arguments))

        execution = next(call[1] for call in self.calls if call[0] == str(self.win) and "ArgumentsBase64" in call[1])
        encoded = re.search(r"-ArgumentsBase64 '([^']+)'", execution).group(1)
        self.assertEqual(arguments, json.loads(base64.b64decode(encoded)))
        self.assertEqual(1, len(self.uploads))

    def test_property_values_mentioning_credentials_keep_normal_argument_passthrough(self):
        arguments = ["help", "-Pmessage=password token secret are literal words", '-Pquoted="value with spaces"']

        self.assertEqual(0, self.invoke(arguments))

        execution = next(call[1] for call in self.calls if call[0] == str(self.win) and "ArgumentsBase64" in call[1])
        encoded = re.search(r"-ArgumentsBase64 '([^']+)'", execution).group(1)
        self.assertEqual(arguments, json.loads(base64.b64decode(encoded)))

    def test_arguments_with_spaces_quotes_and_unicode_round_trip(self):
        arguments = [":app:assembleDebug", "-Pmessage=路径 包含空格", '-Pquoted="value with spaces"', "-Powner=O'Brien"]
        self.assertEqual(0, self.invoke(arguments))
        execution = next(call[1] for call in self.calls if call[0] == str(self.win) and "ArgumentsBase64" in call[1])
        encoded = re.search(r"-ArgumentsBase64 '([^']+)'", execution).group(1)
        self.assertEqual(arguments, json.loads(base64.b64decode(encoded)))
        self.assertIn("Set-Location -LiteralPath", execution)
        self.assertIn("C:/dev/Dusk workspace/snapshots/", execution)
        self.assertNotIn("install", execution.lower())

    def test_property_values_that_mention_install_are_forwarded_without_installing(self):
        arguments = ["help", "-Psample=:app:installDebug"]
        self.assertEqual(0, self.invoke(arguments))
        execution = next(call[1] for call in self.calls if call[0] == str(self.win) and "ArgumentsBase64" in call[1])
        encoded = re.search(r"-ArgumentsBase64 '([^']+)'", execution).group(1)
        self.assertEqual(arguments, json.loads(base64.b64decode(encoded)))

    def test_source_archive_excludes_private_files_and_symlink_targets(self):
        outside = Path(self.directory.name) / "outside-secret"
        outside.write_text("不可同步", encoding="utf-8")
        (self.root / "linked-secret").symlink_to(outside)
        self.assertEqual(0, self.invoke(["help"]))
        names = self.uploads[0]
        self.assertIn("app/src/main/App.kt", names)
        self.assertNotIn("local.properties", names)
        self.assertNotIn("signing/release.jks", names)
        self.assertNotIn("linked-secret", names)
        self.assertNotIn("logs/session.log", names)

    def test_source_archive_is_retained_and_bound_to_manifest_and_remote_hash_check(self):
        self.assertEqual(0, self.invoke(["help"]))
        artifact = next((self.root / "build/wbuild").iterdir())
        archive = artifact / "source.tgz"
        manifest = json.loads((artifact / "source-manifest.json").read_text(encoding="utf-8"))
        self.assertEqual(wbuild.hashlib.sha256(archive.read_bytes()).hexdigest(), manifest["archiveSha256"])
        with tarfile.open(archive, "r:gz") as source:
            for member in source.getmembers():
                self.assertEqual(wbuild.hashlib.sha256(source.extractfile(member).read()).hexdigest(), manifest["files"][member.name])
                self.assertFalse(member.name.startswith("build/"))
        execution = next(call[1] for call in self.calls if call[0] == str(self.win) and "ArgumentsBase64" in call[1])
        self.assertIn("Get-FileHash -LiteralPath source.tgz -Algorithm SHA256", execution)
        self.assertIn(manifest["archiveSha256"], execution)

    def test_each_run_uses_new_snapshot_and_never_reuses_stale_sources(self):
        self.assertEqual(0, self.invoke(["help"]))
        (self.root / "app/src/main/App.kt").rename(self.root / "app/src/main/Renamed.kt")
        self.assertEqual(0, self.invoke(["help"]))
        self.assertIn("app/src/main/App.kt", self.uploads[0])
        self.assertNotIn("app/src/main/App.kt", self.uploads[1])
        self.assertIn("app/src/main/Renamed.kt", self.uploads[1])
        creates = [call[1] for call in self.calls if call[0] == str(self.win) and "New-Item" in call[1]]
        self.assertEqual(2, len(set(creates)))
        self.assertTrue(all("Remove-Item" not in item and "Test-Path" in item for item in creates))

    def test_remote_failure_exit_code_is_not_overwritten_by_report_copy(self):
        self.remote_code = 37
        self.assertEqual(37, self.invoke([":dawn:testDebugUnitTest"]))
        self.assertTrue(list((self.root / "build/wbuild").glob("*/reports.tgz")))

    def test_report_transfer_failure_is_nonzero_and_never_promotes_partial_package(self):
        self.copy_code = 23
        self.assertEqual(23, self.invoke([":app:assembleDebug"]))
        self.assertFalse(list((self.root / "build/wbuild").glob("*/reports.tgz")))

    def test_create_failure_does_not_upload_or_execute(self):
        self.create_code = 19
        self.assertEqual(19, self.invoke(["help"]))
        self.assertEqual(1, len(self.calls))
        self.assertIn("退出码=19", (self.root / "logs/session.log").read_text(encoding="utf-8"))

    def test_report_path_traversal_is_rejected_before_extraction(self):
        self.archive_member = "../../outside.txt"
        with self.assertRaises(ValueError):
            self.invoke(["help"])
        self.assertFalse((self.root / "outside.txt").exists())
        self.assertFalse(list((self.root / "build/wbuild").glob("*/reports.tgz")))
        self.assertIn("退出码=1", (self.root / "logs/session.log").read_text(encoding="utf-8"))

    def test_special_files_and_report_overwrites_of_local_evidence_are_rejected(self):
        for kind, name in ((tarfile.SYMTYPE, "dawn/build/link"), (tarfile.LNKTYPE, "dawn/build/link"),
                           (tarfile.CHRTYPE, "dawn/build/device"), (tarfile.REGTYPE, "source-manifest.json")):
            with self.subTest(kind=kind, name=name):
                self.archive_type = kind
                self.archive_member = name
                with self.assertRaises(ValueError):
                    self.invoke(["help"])

    def test_reused_log_keeps_previous_build_evidence(self):
        log = self.root / "logs/session.log"
        log.parent.mkdir(parents=True)
        log.write_text("前次证据\n", encoding="utf-8")
        self.assertEqual(0, self.invoke(["help"]))
        self.assertTrue(log.read_text(encoding="utf-8").startswith("前次证据\n"))

    def test_install_tasks_are_rejected_without_remote_or_device_side_effects(self):
        for arguments in (["installDebug"], [":app:installDebug"], [":app:installRelease"]):
            with self.subTest(arguments=arguments), contextlib.redirect_stderr(io.StringIO()):
                self.assertEqual(2, self.invoke(arguments))
        self.assertEqual([], self.calls)


class EntryPointTest(unittest.TestCase):
    def test_ci_local_and_jitpack_paths_use_native_entry_without_remote_tools(self):
        script = (ROOT / "gradlew").read_text(encoding="utf-8")
        self.assertIn("WBUILD_LOCAL", script)
        self.assertIn("CI", script)
        self.assertIn("JITPACK", script)
        self.assertIn('gradlew.local" "$@"', script)
        self.assertIn('scripts/wbuild.py" "$@"', script)
        self.assertIn("MINGW*|MSYS*|CYGWIN*", script)

    def test_powershell_restricts_execution_to_valid_snapshot(self):
        script = (ROOT / "scripts/wbuild-run.ps1").read_text(encoding="utf-8-sig")
        pattern = re.search(r"-notmatch '([^']+)'", script).group(1)
        self.assertIn(".Replace([char]92,[char]47) -notmatch", script)
        self.assertRegex("C:/dev/Dusk workspace/snapshots/20261125-123456-abcdef01", pattern)
        self.assertNotRegex("C:/dev/Dusk workspace", pattern)
        self.assertIn("ConvertTo-JavaCommandArgument", script)
        self.assertIn("DisableDelayedExpansion", script)
        self.assertIn("$taskProcess.ExitCode", script)
        self.assertIn("EnvironmentVariables[$name]", script)
        self.assertIn("exit $code", script)
        self.assertNotIn("Remove-Item", script)

    def test_run_streams_output_and_preserves_process_exit_status(self):
        with EvidenceDirectory() as directory:
            log = Path(directory) / "session.log"
            process = mock.MagicMock()
            process.__enter__.return_value = process
            process.stdout = ["第一行\n", "第二行\n"]
            process.wait.return_value = 41
            with mock.patch.object(subprocess, "Popen", return_value=process) as popen, contextlib.redirect_stdout(io.StringIO()):
                self.assertEqual(41, wbuild.run(["fake", "argument with spaces"], log, {"ONLY": "test"}))
            self.assertIn("第一行", log.read_text(encoding="utf-8"))
            self.assertIn("第二行", log.read_text(encoding="utf-8"))
            self.assertEqual(["fake", "argument with spaces"], popen.call_args.args[0])
            self.assertFalse(popen.call_args.kwargs.get("shell", False))

    def test_managed_win_output_is_printed_without_duplication_in_shared_log(self):
        with EvidenceDirectory() as directory:
            log = Path(directory) / "session.log"
            log.write_text("远端入口已写入\n", encoding="utf-8")
            win = str(Path(directory) / "managed-win")
            process = mock.MagicMock()
            process.__enter__.return_value = process
            process.stdout = ["远端入口已写入\n"]
            process.wait.return_value = 7
            with mock.patch.object(subprocess, "Popen", return_value=process), contextlib.redirect_stdout(io.StringIO()) as console:
                self.assertEqual(7, wbuild.run([win, "command"], log, {"WBUILD_WIN": win}))
            self.assertEqual("远端入口已写入\n", console.getvalue())
            self.assertEqual("远端入口已写入\n", log.read_text(encoding="utf-8"))

    def test_native_environment_routes_execute_the_stub_without_remote_tools(self):
        with EvidenceDirectory(prefix="entry ") as directory:
            root = Path(directory)
            (root / "gradlew").write_text((ROOT / "gradlew").read_text(encoding="utf-8"), encoding="utf-8")
            (root / "gradlew.local").write_text('printf "%s\\n" "$@"\n', encoding="utf-8")
            arguments = ["help", "-Pmessage=路径 包含空格", '-Pquoted="value with spaces"']
            for key, value in (("CI", "true"), ("CI", "1"), ("GITHUB_ACTIONS", "true"), ("JITPACK", "1"), ("WBUILD_LOCAL", "1")):
                with self.subTest(key=key, value=value):
                    environment = dict(os.environ)
                    for name in ("CI", "GITHUB_ACTIONS", "JITPACK", "WBUILD_LOCAL"):
                        environment.pop(name, None)
                    environment[key] = value
                    result = subprocess.run(["bash", str(root / "gradlew"), *arguments], text=True, capture_output=True, env=environment)
                    self.assertEqual(0, result.returncode, result.stderr)
                    self.assertEqual(arguments, result.stdout.splitlines())
                    if key == "WBUILD_LOCAL":
                        self.assertIn("显式改为本机构建", result.stderr)


if __name__ == "__main__":
    unittest.main()

