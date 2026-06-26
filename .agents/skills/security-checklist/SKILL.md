---
name: security-checklist
description: Security checklist and vulnerability verification for this project
---

# Security Checklist

## Verification Items

### 1. Hardcoded Secrets
- [ ] No API Key, Secret, Password in code?
- [ ] Using environment variables or config files?

Verification commands:
```bash
# Basic search in Java files
grep -r "password.*=.*\"" --include="*.java"
grep -r "secret.*=.*\"" --include="*.java"
grep -r "apiKey.*=.*\"" --include="*.java"

# Check YAML/Properties files
grep -r "password\|secret\|apiKey" --include="*.yml" --include="*.yaml" --include="*.properties"
```

### 2. SQL Injection
- [ ] Using PreparedStatement or JPA/QueryDSL?
- [ ] Not concatenating SQL strings directly?

### 3. JWT Verification
- [ ] Verifying JWT signature?
- [ ] Checking expiration time?
- [ ] Validating claims?

### 4. API Key Security
- [ ] Masking API Key in responses?
- [ ] Encrypting API Key when storing?

### 5. Logging
- [ ] Not logging sensitive info (password, token, etc.)?
- [ ] Appropriate log level?

### 6. Authorization
- [ ] Using `@PreAuthorize` or Security Filter for auth-required endpoints?
- [ ] Verifying access to own resources only?

## References

- `src/main/java/team/themoment/readygsmserver/global/security/` - Security configuration (JWT, OAuth2)
- `src/main/java/team/themoment/readygsmserver/global/security/jwt/JwtFilter.java` - JWT verification example
