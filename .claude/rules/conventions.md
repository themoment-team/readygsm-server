# Package Structure

```
team.themoment.readygsmserver
├── global/          # Cross-domain shared configuration
│   ├── config/      # Spring configuration classes (@Configuration)
│   └── security/    # JWT, OAuth2 related classes
└── domain/          # Domain-specific packages
    └── {domain}/
        ├── controller/
        ├── service/
        ├── domain/
        └── repository/
```

API paths follow the `/v1/{domain}/...` pattern.

---

# Commit Convention

Commit message format: `type(scope): description`

## Types

| Type | Description |
|---|---|
| `add` | Add new feature |
| `update` | Modify existing feature |
| `fix` | Bug fix |
| `refactor` | Refactoring |
| `test` | Test code |
| `docs` | Documentation change |
| `merge` | Branch merge |

## Scopes

**Priority: domain name > global**

| Scope | Usage |
|---|---|
| `auth` | Authentication / JWT / OAuth2 |
| `student` | Student information |
| `user` | User account management |
| `oauth` | OAuth2 client |
| `global` | Cross-domain concerns (config, security, etc.) |
| `ci/cd` | Build / deployment |

## Message Rules

- No period at the end
- Write in imperative or noun phrase form

## Good / Bad Examples

```
✅ fix(auth): JWT 만료 예외 처리 수정
❌ fix(global): JWT 만료 예외 처리 수정   ← use domain scope first

✅ refactor(global): 공통 예외 처리 로직 개선
❌ update(global): 학생 엔티티 수정       ← use student scope
```
