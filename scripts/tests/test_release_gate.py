"""发布门禁的离线回归，不访问发布渠道。"""

import importlib.util
import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch
from urllib.error import URLError

SPEC = importlib.util.spec_from_file_location("release_gate", Path(__file__).parents[1] / "release_gate.py")
gate = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(gate)
COMMIT = "a" * 40
CHECKS = dict.fromkeys(gate.REQUIRED_CHECKS, "passed")


class ReleaseGateTest(unittest.TestCase):
    def setUp(self):
        self.root = Path(tempfile.mkdtemp(prefix="duskmvx-release-gate-"))
        self.manifest = self.root / "manifest.json"
        for suffix in (".pom", ".aar", ".module", "-sources.jar"):
            (self.root / ("candidate" + suffix)).write_bytes(b"candidate")
        for module in gate.REQUIRED_TEST_MODULES:
            report = self.module_report(module)
            report.parent.mkdir(parents=True)
            report.write_text(
                '<testsuite tests="3" failures="0" errors="0" skipped="0"/>', encoding="utf-8")
        consumer = self.root / "reports" / "consumer"
        (consumer / "test-results").mkdir(parents=True)
        (consumer / "test-results" / "TEST-consumer.xml").write_text(
            '<testsuite tests="2" failures="0" errors="0" skipped="0"/>', encoding="utf-8")
        (consumer / "consumer-dependencies.txt").write_text("候选依赖验证通过", encoding="utf-8")

    def record(self):
        return gate.record_candidate(self.root, self.manifest, COMMIT, CHECKS)

    def module_report(self, module):
        return self.root / "reports" / module / "test-results" / ("TEST-" + module + ".xml")

    def response(self, **changes):
        result = {"status": "ok", "commit": COMMIT, "version": "v0.0.30", "modules": ["dawn"]}
        result.update(changes)
        return json.dumps(result)

    def policy(self):
        return {
            "environment": {"protection_rules": [
                {"type": "required_reviewers", "reviewers": [{"type": "Team", "reviewer": {"id": 1}}]},
            ]},
            "rulesets": [{
                "target": "tag", "enforcement": "active",
                "conditions": {"ref_name": {"include": ["~ALL"], "exclude": []}},
                "bypass_actors": [{"actor_type": "Integration", "actor_id": 1, "bypass_mode": "always"}],
                "rules": [{"type": "creation"}],
            }],
        }

    def test_recorded_candidate_matches_commit_and_every_file(self):
        expected = self.record()

        actual = gate.verify_candidate(self.root, self.manifest, COMMIT)

        self.assertEqual(expected, actual)
        self.assertEqual(6 + len(gate.REQUIRED_TEST_MODULES), len(actual["sha256"]))
        self.assertEqual(2 + 3 * len(gate.REQUIRED_TEST_MODULES), actual["tests"])
        self.assertEqual(2, actual["consumerTests"])
        self.assertEqual(dict.fromkeys(gate.REQUIRED_TEST_MODULES, 3), actual["moduleTests"])

    def test_another_commit_is_rejected(self):
        self.record()

        with self.assertRaises(gate.GateError):
            gate.verify_candidate(self.root, self.manifest, "b" * 40)

    def test_changed_or_added_artifacts_are_rejected(self):
        self.record()
        (self.root / "candidate.aar").write_bytes(b"changed")

        with self.assertRaises(gate.GateError):
            gate.verify_candidate(self.root, self.manifest, COMMIT)

        self.record()
        (self.root / "unexpected.jar").write_bytes(b"unexpected")
        with self.assertRaises(gate.GateError):
            gate.verify_candidate(self.root, self.manifest, COMMIT)

    def test_missing_artifact_is_rejected(self):
        (self.root / "candidate.module").rename(self.root / "candidate.metadata")

        with self.assertRaises(gate.GateError):
            self.record()

    def test_failed_or_missing_check_cannot_record_candidate(self):
        for checks in ({"build": "passed"}, {**CHECKS, "tests": "failed"}, {**CHECKS, "lint": "skipped"}):
            with self.subTest(checks=checks), self.assertRaises(gate.GateError):
                gate.record_candidate(self.root, self.manifest, COMMIT, checks)

    def test_failed_check_in_downloaded_manifest_is_rejected(self):
        evidence = self.record()
        evidence["checks"]["tests"] = "failed"
        self.manifest.write_text(json.dumps(evidence), encoding="utf-8")

        with self.assertRaises(gate.GateError):
            gate.verify_candidate(self.root, self.manifest, COMMIT)

    def test_invalid_manifest_is_rejected(self):
        self.manifest.write_text("not json", encoding="utf-8")

        with self.assertRaises(gate.GateError):
            gate.verify_candidate(self.root, self.manifest, COMMIT)

    def test_no_source_skipped_or_failed_tests_do_not_pass(self):
        report = self.module_report("dawn")
        for values in ((0, 0, 0), (3, 0, 3), (3, 0, 1), (3, 1, 0)):
            tests, failures, skipped = values
            report.write_text(
                f'<testsuite tests="{tests}" failures="{failures}" skipped="{skipped}"/>', encoding="utf-8")
            with self.subTest(values=values), self.assertRaises(gate.GateError):
                self.record()

    def test_missing_required_module_report_cannot_be_hidden_by_other_passing_tests(self):
        for module in gate.REQUIRED_TEST_MODULES:
            report = self.module_report(module)
            original = report.read_bytes()
            report.rename(report.with_suffix(".missing"))
            try:
                with self.subTest(module=module), self.assertRaises(gate.GateError):
                    self.record()
            finally:
                report.write_bytes(original)

    def test_zero_skipped_failed_or_invalid_required_module_reports_are_rejected(self):
        reports = (
            '<testsuite tests="0"/>',
            '<testsuite tests="3" skipped="1"/>',
            '<testsuite tests="3" failures="1"/>',
            '<testsuite tests="3" errors="1"/>',
            '<testsuites tests="3"/>',
            '<testsuites tests="1" failures="0" errors="0" skipped="0"><testsuite tests="1" failures="1" errors="0" skipped="0"/></testsuites>',
            '<testsuites tests="2"><testsuite tests="1"/></testsuites>',
            '<testsuite tests="invalid"/>',
            'not xml',
        )
        for module in gate.REQUIRED_TEST_MODULES:
            report = self.module_report(module)
            original = report.read_bytes()
            try:
                for content in reports:
                    report.write_text(content, encoding="utf-8")
                    with self.subTest(module=module, content=content), self.assertRaises(gate.GateError):
                        self.record()
            finally:
                report.write_bytes(original)

    def test_android_instrumentation_testsuites_report_is_counted(self):
        report = self.module_report("dawn-device")
        report.write_text(
            "<?xml version='1.0' encoding='UTF-8' ?>"
            '<testsuites tests="1" failures="0" errors="0" skipped="0">'
            '<testsuite name="com.lzq.dawn.lifecycle.DevicePageLifecycleTest" '
            'tests="1" failures="0" errors="0" skipped="0">'
            '<testcase name="deviceLifecycle"/></testsuite></testsuites>', encoding="utf-8")

        self.assertEqual(1, gate.count_tests(report.parent))
        self.assertEqual(1, self.record()["moduleTests"]["dawn-device"])

    def test_downloaded_manifest_requires_matching_module_counts(self):
        evidence = self.record()
        evidence["moduleTests"]["dawn"] = 0
        self.manifest.write_text(json.dumps(evidence), encoding="utf-8")

        with self.assertRaises(gate.GateError):
            gate.verify_candidate(self.root, self.manifest, COMMIT)

    def test_missing_consumer_test_report_blocks_candidate(self):
        report = self.root / "reports" / "consumer" / "test-results" / "TEST-consumer.xml"
        report.rename(report.with_suffix(".not-test"))

        with self.assertRaises(gate.GateError):
            self.record()

    def test_missing_consumer_dependency_report_blocks_candidate(self):
        report = self.root / "reports" / "consumer" / "consumer-dependencies.txt"
        report.rename(report.with_suffix(".missing"))

        with self.assertRaises(gate.GateError):
            self.record()

    def test_zero_skipped_failed_or_insufficient_consumer_tests_are_rejected(self):
        report = self.root / "reports" / "consumer" / "test-results" / "TEST-consumer.xml"
        for tests, failures, skipped in ((0, 0, 0), (1, 0, 0), (2, 1, 0), (2, 0, 1)):
            report.write_text(
                f'<testsuite tests="{tests}" failures="{failures}" skipped="{skipped}"/>', encoding="utf-8")
            with self.subTest(tests=tests, failures=failures, skipped=skipped), self.assertRaises(gate.GateError):
                self.record()

    def test_release_tag_matches_candidate_version(self):
        for version in ("0.0.30", "1.2.3-rc.1"):
            for tag in (version, "v" + version):
                with self.subTest(version=version, tag=tag):
                    self.assertEqual(tag, gate.validate_release_tag(tag, version))

    def test_wrong_or_unsafe_release_tags_are_rejected(self):
        for tag in ("v0.0.31", "V0.0.30", "refs/tags/v0.0.30", "v0.0.30/extra", "$(command)", ""):
            with self.subTest(tag=tag), self.assertRaises(gate.GateError):
                gate.validate_release_tag(tag, "0.0.30")
        for version in ("", "0.0.30/extra", "anything"):
            with self.subTest(version=version), self.assertRaises(gate.GateError):
                gate.validate_release_tag(version, version)

    def test_reviewed_environment_and_all_tag_creation_rule_pass(self):
        self.assertIsNone(gate.validate_release_policy(self.policy()))

    def test_missing_or_empty_release_reviewers_are_rejected(self):
        for protections in ([], None, [{"type": "required_reviewers", "reviewers": []}],
                            [{"type": "required_reviewers", "reviewers": "invalid"}]):
            policy = self.policy()
            policy["environment"]["protection_rules"] = protections
            with self.subTest(protections=protections), self.assertRaises(gate.GateError):
                gate.validate_release_policy(policy)

    def test_partial_disabled_or_non_creation_tag_rules_do_not_pass(self):
        changes = (
            {"target": "branch"}, {"enforcement": "disabled"}, {"enforcement": "evaluate"},
            {"rules": [{"type": "update"}]}, {"rules": []},
            {"conditions": {"ref_name": {"include": ["refs/tags/v*"], "exclude": []}}},
            {"conditions": {"ref_name": {"include": ["~ALL"], "exclude": ["refs/tags/v*"]}}},
            {"conditions": None},
        )
        for change in changes:
            policy = self.policy()
            policy["rulesets"][0].update(change)
            with self.subTest(change=change), self.assertRaises(gate.GateError):
                gate.validate_release_policy(policy)

    def test_malformed_or_missing_remote_policy_is_rejected(self):
        for policy in (None, [], {}, {"environment": []},
                       {"environment": self.policy()["environment"], "rulesets": None}):
            with self.subTest(policy=policy), self.assertRaises(gate.GateError):
                gate.validate_release_policy(policy)

    def test_administrator_human_or_broad_bypass_does_not_pass(self):
        invalid_bypasses = (
            None,
            [{"actor_type": "RepositoryRole", "actor_id": 5, "bypass_mode": "always"}],
            [{"actor_type": "OrganizationAdmin", "actor_id": 1, "bypass_mode": "always"}],
            [{"actor_type": "Team", "actor_id": 1, "bypass_mode": "always"}],
            [{"actor_type": "Integration", "actor_id": 1, "bypass_mode": "pull_request"}],
            [{"actor_type": "Integration", "actor_id": 0, "bypass_mode": "always"}],
            [{"actor_type": "Integration", "actor_id": True, "bypass_mode": "always"}],
            [{"actor_type": "Integration", "actor_id": 1, "bypass_mode": "always"}] * 2,
        )
        for actors in invalid_bypasses:
            policy = self.policy()
            policy["rulesets"][0]["bypass_actors"] = actors
            with self.subTest(actors=actors), self.assertRaises(gate.GateError):
                gate.validate_release_policy(policy)

    def test_tag_authorization_requires_verified_candidate_policy_and_version(self):
        self.record()

        tag = gate.authorize_tag_creation(self.root, self.manifest, COMMIT, self.policy(), "v0.0.30", "0.0.30")

        self.assertEqual("v0.0.30", tag)

    def test_changed_candidate_cannot_authorize_tag_creation(self):
        self.record()
        (self.root / "candidate.aar").write_bytes(b"changed")

        with self.assertRaises(gate.GateError):
            gate.authorize_tag_creation(self.root, self.manifest, COMMIT, self.policy(), "v0.0.30", "0.0.30")

    def test_wrong_commit_unprotected_policy_or_wrong_version_cannot_authorize_tag(self):
        self.record()
        for commit, policy, tag in (
                ("b" * 40, self.policy(), "v0.0.30"),
                (COMMIT, {}, "v0.0.30"),
                (COMMIT, self.policy(), "v0.0.31")):
            with self.subTest(commit=commit, policy=policy, tag=tag), self.assertRaises(gate.GateError):
                gate.authorize_tag_creation(self.root, self.manifest, commit, policy, tag, "0.0.30")

    def test_tag_authorization_command_requires_readable_policy_evidence(self):
        policy_path = self.root / "release-policy.json"
        policy_path.write_text(json.dumps(self.policy()), encoding="utf-8")
        self.record()
        arguments = ["authorize-tag", "--root", str(self.root), "--manifest", str(self.manifest),
                     "--commit", COMMIT, "--policy", str(policy_path), "--tag", "v0.0.30", "--version", "0.0.30"]

        with patch("builtins.print"):
            self.assertEqual(0, gate.main(arguments))
            policy_path.rename(policy_path.with_suffix(".missing"))
            self.assertEqual(1, gate.main(arguments))
            policy_path.write_text("invalid", encoding="utf-8")
            self.assertEqual(1, gate.main(arguments))

    def test_http_success_requires_matching_build_commit_tag_and_module(self):
        result = gate.validate_jitpack(200, self.response(), COMMIT, "v0.0.30", "dawn")

        self.assertEqual("ok", result["status"])

    def test_http_failure_is_rejected_even_with_success_json(self):
        for status in (302, 400, 404, 500, 503):
            with self.subTest(status=status), self.assertRaises(gate.GateError):
                gate.validate_jitpack(status, self.response(), COMMIT, "v0.0.30", "dawn")

    def test_invalid_json_or_non_object_is_rejected(self):
        for body in ("<html>failure</html>", "null", "[]", "{}"):
            with self.subTest(body=body), self.assertRaises(gate.GateError):
                gate.validate_jitpack(200, body, COMMIT, "v0.0.30", "dawn")

    def test_wrong_build_status_or_identity_is_rejected(self):
        for changes in ({"status": "error"}, {"status": "building"}, {"commit": "b" * 40},
                        {"version": "other"}, {"modules": []}, {"modules": "dawn"}):
            with self.subTest(changes=changes), self.assertRaises(gate.GateError):
                gate.validate_jitpack(200, self.response(**changes), COMMIT, "v0.0.30", "dawn")

    @patch.object(gate, "urlopen", side_effect=URLError("offline"))
    def test_network_failure_is_visible(self, request):
        with self.assertRaises(gate.GateError):
            gate.fetch_jitpack("https://jitpack.io/api/builds/com.github.asasa868/DuskMVX/v0.0.30",
                               COMMIT, "v0.0.30", "dawn")
        request.assert_called_once()

    def test_other_query_destinations_are_rejected(self):
        with self.assertRaises(gate.GateError):
            gate.fetch_jitpack("https://example.invalid/builds", COMMIT, "v0.0.30", "dawn")


if __name__ == "__main__":
    unittest.main()

