---
description: Create Git commits by splitting changes into logical units
allowed-tools: Bash
---

Create Git commits following these rules:

- Follow commit message format: `type(scope): description`
    - Types: add/update/fix/refactor/test/docs/merge
    - Scopes:
        - **Primary**: Domain names (auth, student, user, oauth)
        - **Cross-cutting concerns only**: global
    - Description: Korean, no period, do NOT use any of these endings: "~한다/~된다", "~하기/~하기 위해", "~합니다/~됩니다", "~했습니다"
    - Use concise verb stem or noun phrase: e.g., "엔티티 필드 추가", "트랜잭션 롤백 방지", "로직 개선"
- Use subject line only (no commit body)
- Do NOT add Claude as co-author
- Split changes into appropriate logical units with multiple commits
- Each commit should have a single responsibility

## Scope Selection Guide

**Priority**: Domain name > global

**Domain names (Primary)**:

- auth: Authentication/JWT/OAuth2 flow
- student: Student information
- user: User account management
- oauth: OAuth2 client management

**Cross-cutting concerns only**:

- global: Affects multiple domains (config, security, common utilities)
- ci/cd: Build/deployment

**Wrong Examples**:

- `fix(global): JWT 만료 예외 처리 수정` → `fix(auth): JWT 만료 예외 처리 수정`
- `update(global): 학생 엔티티 수정` → `update(student): 엔티티 필드 추가`

**Correct Cross-cutting Usage**:

- `refactor(global): 공통 예외 처리 로직 개선`
- `update(ci/cd): GitHub Actions 워크플로우 최적화`

Steps:

1. Check changes with `git status` and `git diff`
2. Categorize changes into logical units (e.g., feature addition, bug fix, refactoring)
3. Group files by each unit
4. For each group:
    - Stage only relevant files with `git add`
    - Write concise commit message following conventions (subject only)
    - Execute `git commit -m "message"`
5. Verify results with `git log --oneline -n [number of commits]`
