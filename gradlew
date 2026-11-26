#!/usr/bin/env bash
# 开发机默认转发到隔离构建快照，持续集成及显式本机模式使用原生入口。
set -euo pipefail
TASK_ROOT="$(cd "$(dirname "$0")" && pwd)"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) exec bash "$TASK_ROOT/gradlew.local" "$@";; esac
if [[ "${WBUILD_LOCAL:-0}" == 1 ]]; then
    echo '显式改为本机构建。' >&2
    exec bash "$TASK_ROOT/gradlew.local" "$@"
fi
if [[ "${CI:-}" == true || "${CI:-}" == 1 || "${GITHUB_ACTIONS:-}" == true || -n "${JITPACK:-}" ]]; then
    exec bash "$TASK_ROOT/gradlew.local" "$@"
fi
exec python3 "$TASK_ROOT/scripts/wbuild.py" "$@"
