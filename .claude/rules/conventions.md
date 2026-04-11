# 패키지 구조 원칙

```
team.themoment.readygsmserver
├── global/          # 도메인에 무관한 공통 설정
│   ├── config/      # Spring 설정 클래스 (@Configuration)
│   └── security/    # JWT, OAuth2 관련 클래스
└── domain/          # 도메인별 패키지
    └── {domain}/
        ├── controller/
        ├── service/
        ├── domain/
        └── repository/
```

API 경로는 `/v1/{domain}/...` 패턴을 사용합니다.

---

# 커밋 컨벤션

커밋 메시지 형식: `type(scope): description`

## 타입

| 타입 | 설명 |
|---|---|
| `add` | 새 기능 추가 |
| `update` | 기존 기능 변경 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 |
| `test` | 테스트 코드 |
| `docs` | 문서 변경 |
| `merge` | 브랜치 병합 |

## 스코프

**우선순위: 도메인명 > global**

| 스코프 | 사용 기준 |
|---|---|
| `auth` | 인증/JWT/OAuth2 |
| `student` | 학생 정보 |
| `user` | 사용자 계정 관리 |
| `oauth` | OAuth2 클라이언트 |
| `global` | 여러 도메인에 걸친 공통 관심사 (config, security 등) |
| `ci/cd` | 빌드/배포 |

## Description 규칙

- 한국어, 마침표 없음
- 금지 어미: `~한다/~된다`, `~하기`, `~합니다/~됩니다`, `~했습니다`
- 권장 표현: 동사 원형 또는 명사구 (예: "엔티티 필드 추가", "로직 개선")

## 올바른 예 / 잘못된 예

```
✅ fix(auth): JWT 만료 예외 처리 수정
❌ fix(global): JWT 만료 예외 처리 수정   ← 도메인명 우선

✅ refactor(global): 공통 예외 처리 로직 개선
❌ update(global): 학생 엔티티 수정       ← student 스코프 사용
```