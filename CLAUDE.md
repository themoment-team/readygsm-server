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

**기술 스택**: Spring Boot 4.0.5 · Java 21 · MySQL 8 · Redis 7 · Spring Security 6

**인프라**: `compose.yml`에 MySQL·Redis가 정의되어 있으며, `spring-boot-docker-compose` 의존성으로 `bootRun` 시 자동으로 컨테이너가 시작/중지됩니다.

### the-sdk (com.github.themoment-team:the-sdk:1.5)

이 프로젝트의 핵심 공통 라이브러리입니다. `application.yml`의 `sdk.*` 설정으로 제어합니다.

- **Logging**: 모든 HTTP 요청/응답에 UUID `Log-ID` 부여, 자동 로깅
- **Response Wrapper**: 컨트롤러 반환값을 `CommonApiResponse`로 자동 래핑. `success()` / `created()` / `error()` 팩토리 메서드 사용
- **Exception Handler**: `ExpectedException(message, HttpStatus)`를 throw하면 표준 에러 응답 자동 반환
- **Swagger**: `/v3/api-docs`, `/swagger-ui` 경로에 자동 구성. `/v1/**` 경로만 문서화

### 인증 흐름

```
OAuth2 로그인 → CustomOAuth2UserService (유저 처리)
             → OAuth2SuccessHandler (JWT 발급 → HttpOnly 쿠키)
             → OAuth2FailureHandler (401 반환)

API 요청 → JwtFilter (Authorization: Bearer <token> 파싱)
         → SecurityContextHolder에 인증 정보 설정
```

Refresh Token은 Redis에 저장하는 구조로 설계되어 있으며, `OAuth2SuccessHandler`에 TODO로 표시된 부분에 구현합니다.

### 패키지 구조 원칙

```
team.themoment.readygsmserver
├── global/          # 도메인에 무관한 공통 설정
│   ├── config/      # Spring 설정 클래스 (@Configuration)
│   └── security/    # JWT, OAuth2 관련 클래스
└── domain/          # 도메인별 패키지 (추후 추가)
    └── {domain}/
        ├── controller/
        ├── service/
        ├── domain/
        └── repository/
```

API 경로는 `/v1/{domain}/...` 패턴을 사용합니다.