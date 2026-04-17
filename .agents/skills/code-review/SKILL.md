---
name: code-review
description: Code review checklist and automatic verification for Java + Spring Boot code
---

# Code Review Guide

Analyze changed files and verify the following items.

## Check Changes

1. Run `git diff` or `git diff master...HEAD` to see changed files
2. Read each file for detailed analysis

## Checklist

### DTO
- [ ] DTO name has `ReqDto` or `ResDto` suffix?
- [ ] Using `@JsonProperty` where needed?

### Java Style
- [ ] Using constructor injection (not field injection)?
- [ ] Using Lombok annotations (`@RequiredArgsConstructor`, `@Getter`, etc.) appropriately?
- [ ] Avoiding unnecessary mutability?

### JPA/Database
- [ ] Applied `@Transactional(readOnly = true)` for read operations?
- [ ] No N+1 problem? (Need Fetch Join?)

### Test
- [ ] Test code written?
- [ ] Following Given-When-Then pattern?
- [ ] Using Mockito verify appropriately?

### Commit
- [ ] Following commit message convention?
- [ ] Using domain name for scope? (module names allowed only for cross-cutting concerns)
- [ ] Commits split into logical units?

### Other
- [ ] No excessive comments?
- [ ] No hardcoded secrets?
- [ ] No sensitive information in logs?

## Report Format

For each item:
- ✓ Pass
- ⚠ Warning (recommendation)
- ✗ Error (needs fix)

Final summary:
- Total {n} items verified
- {p} passed, {w} warnings, {e} errors
