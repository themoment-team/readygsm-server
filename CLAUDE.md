# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run (Docker Compose auto-starts MySQL and Redis)
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "team.themoment.readygsmserver.SomeTest"
```

## Environment Variables

The following environment variables are required before running.

| Variable | Description |
|---|---|
| `JWT_SECRET` | Base64-encoded HMAC secret key (256-bit or more) |
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |
| `GOOGLE_REDIRECT_URI` | Google OAuth2 redirect URI (must match Google Console registration, e.g. `http://localhost:3000/oauth/callback`) |
| `KAKAO_CLIENT_ID` | Kakao OAuth2 REST API key |
| `KAKAO_CLIENT_SECRET` | Kakao OAuth2 client secret |