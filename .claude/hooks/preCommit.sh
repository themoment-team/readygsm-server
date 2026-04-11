#!/bin/bash
# .claude/hooks/preCommit.sh
# Validate commit message format before committing

if [[ "$TOOL_NAME" != "Bash" ]]; then
    exit 0
fi

COMMAND="$TOOL_PARAMS_COMMAND"

if [[ ! "$COMMAND" =~ git[[:space:]]+commit ]]; then
    exit 0
fi

# Extract commit message - handles both -m "..." and heredoc $(cat <<'EOF'...EOF)
if [[ "$COMMAND" =~ \$\(cat[[:space:]]+\<\<\'EOF\' ]]; then
    COMMIT_MSG=$(printf '%s' "$COMMAND" | sed -n "/<<'EOF'/,/^EOF/{/<<'EOF'/d;/^[[:space:]]*EOF/d;p}" | head -n 1)
else
    COMMIT_MSG=$(printf '%s' "$COMMAND" | sed -n -e 's/.*-m[[:space:]]*"\([^"]*\)".*/\1/p' -e "s/.*-m[[:space:]]*'\([^']*\)'.*/\1/p" | head -n 1)
fi

if [[ -z "$COMMIT_MSG" ]]; then
    exit 0
fi

PATTERN="^(add|update|fix|refactor|test|docs|merge)\\(([^)]+)\\): .+"
if [[ ! "$COMMIT_MSG" =~ $PATTERN ]]; then
    echo "[Hook] ✗ Invalid commit message format"
    echo "Expected: type(scope): description"
    echo "Example: fix(auth): JWT 만료 예외 처리 수정"
    echo ""
    echo "Types: add/update/fix/refactor/test/docs/merge"
    echo "Scopes: auth, student, user, oauth, global, ci/cd"
    exit 2
fi

echo "[Hook] ✓ Commit message format valid"