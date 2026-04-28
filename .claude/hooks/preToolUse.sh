#!/bin/bash

INPUT=$(cat)

if ! command -v jq &>/dev/null; then
    echo "[Hook] Error: 'jq' is required but not installed." >&2
    exit 1
fi

TOOL_NAME=$(echo "$INPUT" | jq -r '.tool_name // ""')

if [[ "$TOOL_NAME" == "Bash" ]]; then
    COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command // ""')
    LOG_FILE="$(cd "$(dirname "$0")/.." && pwd)/bash-log.txt"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $COMMAND" >> "$LOG_FILE"

    BLOCKED_PATTERNS=(
        "rm -rf /"
        "sudo rm"
        "> /dev/"
        "dd if="
        "mkfs"
        "curl.*\| sh"
        "wget.*\| sh"
    )
    for pattern in "${BLOCKED_PATTERNS[@]}"; do
        if [[ "$COMMAND" =~ $pattern ]]; then
            echo "[Hook] Blocked dangerous command: $COMMAND"
            exit 2
        fi
    done
fi