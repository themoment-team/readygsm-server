# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# 빌드
./gradlew build

# 실행 (Docker Compose로 MySQL·Redis 자동 시작)
./gradlew bootRun

# 전체 테스트
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "team.themoment.readygsmserver.SomeTest"
```

## 환경 변수

실행 전 아래 환경 변수가 필요합니다.

| 변수 | 설명 |
|---|---|
| `JWT_SECRET` | Base64 인코딩된 256bit 이상 HMAC 시크릿 키 |
| `GOOGLE_CLIENT_ID` | Google OAuth2 클라이언트 ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 클라이언트 시크릿 |

## 아키텍처

**인프라**: `compose.yml`에 MySQL·Redis가 정의되어 있으며, `spring-boot-docker-compose` 의존성으로 `bootRun` 시 자동으로 컨테이너가 시작/중지됩니다.