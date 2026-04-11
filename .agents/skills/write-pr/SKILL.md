---
name: write-pr
description: Generate PR body and title suggestions based on commits diff from master branch
---

## Context gathering

Run the following to collect context:

```bash
git branch --show-current
git log master..HEAD --oneline
git diff master...HEAD --stat
git diff master...HEAD
find src/main/java -mindepth 5 -maxdepth 5 -type d | sed 's|.*domain/||'
```

Read `.github/PULL_REQUEST_TEMPLATE.md` for the PR template structure.

## PR Title Convention

This project uses the following PR title format: `[scope] description`

**Scope Selection Rule:**
- Use domain names by default for feature-specific changes (discover from `src/main/java/.../domain/` directory structure)
- Only use `global` when changes affect multiple domains or cross-cutting concerns (e.g., config, security, common utilities)
- Use `ci/cd` for build/deployment changes

**Recent PR Title Examples:**
- `[global] 공통 예외 처리 로직 추가`
- `[auth] JWT 토큰 발급 및 갱신 기능 구현`
- `[ci/cd] GitHub Actions 빌드 파이프라인 추가`

## Your task

Based on the above information, perform the following tasks:

1. **PR Title Suggestions**:
   - Suggest 3 appropriate titles based on the convention above
   - Format: `[scope] description`
   - Description: Korean, clear and concise
   - No emojis

2. **PR Body**:
   - Follow the PR template structure
   - Analyze commits and changes between master and current branch
   - Total length must not exceed 2500 characters
   - No emojis
   - Write in Korean
   - Be clear and specific

3. **Writing Style**:
   - Use formal Korean ending style: "~하였습니다", "~되었습니다", "~추가하였습니다" (not "~했어요", "~합니다", "~했습니다")

4. **Save to file**:
   - Save the content to `PR_BODY.md`
   - Overwrite if file already exists

5. **Output format**:

   ```
   ## 추천 PR 제목

   1. [title1]
   2. [title2]
   3. [title3]

   ## PR 본문 (PR_BODY.md에 저장됨)

   [preview of generated content]
   ```