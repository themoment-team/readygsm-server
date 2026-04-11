---
description: Create Git commits by splitting changes into logical units
allowed-tools: Bash
---

Create Git commits following the commit convention defined in `.claude/rules/conventions.md`.

Key rules:
- Format: `type(scope): description`
- Use subject line only (no commit body)
- Do NOT add Claude as co-author
- Split changes into appropriate logical units with multiple commits
- Each commit should have a single responsibility

Steps:

1. Check changes with `git status` and `git diff`
2. Categorize changes into logical units (e.g., feature addition, bug fix, refactoring)
3. Group files by each unit
4. For each group:
    - Stage only relevant files with `git add`
    - Write concise commit message following conventions (subject only)
    - Execute `git commit -m "message"`
5. Verify results with `git log --oneline -n [number of commits]`