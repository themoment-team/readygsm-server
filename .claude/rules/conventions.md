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