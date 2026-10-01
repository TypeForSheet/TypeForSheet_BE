# TypeForSheet Backend

TypeForSheet의 Spring Boot 기반 REST API 서버입니다.

## 기술 스택

- Java 21
- Spring Boot 4.1.1
- Gradle
- Spring Web MVC
- Spring Data JPA
- MySQL 8.4
- Flyway
- Springdoc OpenAPI
- Spring Boot Actuator

## 로컬 실행

### 요구사항

- Java 21
- Docker 및 Docker Compose

### 데이터베이스 실행

```bash
cp .env.example .env
docker compose up -d
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

- Health check: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 테스트와 코드 스타일

```bash
./gradlew test
./gradlew clean build
./gradlew spotlessApply
./gradlew spotlessCheck
```

## 패키지 구조

도메인별로 일반적인 계층형 구조를 사용합니다. 필요한 도메인이 생길 때 패키지를 추가합니다.

```text
com.typeforsheet.backend
├── global
│   ├── config
│   ├── error
│   ├── response
│   └── util
└── {domain}
    ├── controller
    ├── service
    ├── repository
    ├── entity
    └── dto
        ├── request
        └── response
```

## 브랜치 및 PR 규칙

- 브랜치: `{type}/TFS-{github-issue-number}`
- 예시: `feat/TFS-12`
- PR 제목: `[FEAT] #12 시트 생성 API 구현`
- PR 본문에 `Closes #12`를 작성합니다.
- 모든 변경은 PR을 통해 `dev`에 병합합니다.
- `main`에는 원칙적으로 `dev`만 병합합니다.

자세한 규칙은 [CONTRIBUTING.md](CONTRIBUTING.md)를 확인해 주세요.
