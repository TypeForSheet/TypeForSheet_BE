
# Contributing

## 작업 순서

1. GitHub Issue를 생성하고 자동 발급된 MUBLE 작업 번호를 확인합니다.
2. 최신 `dev`에서 MUBLE 작업 번호를 사용하여 브랜치를 생성합니다.
3. 구현과 테스트를 완료한 뒤 원격 브랜치로 Push합니다.
4. `dev`를 대상으로 Pull Request를 생성합니다.
5. CI와 CodeRabbit 리뷰를 확인합니다.
6. 사람 승인 1명 이상을 받고 모든 Review Conversation을 해결합니다.
7. Squash merge 후 작업 브랜치를 삭제합니다.

## 작업 번호 규칙

MUBLE 작업 번호는 GitHub Issue/PR 번호와 별도로 관리합니다.

- 새로운 Issue에만 MUBLE 작업 번호를 순차 발급합니다.
- PR은 연결된 Issue의 MUBLE 작업 번호를 사용합니다.
- 기존 TFS 작업 번호는 유지한 채 MUBLE 접두사로 변경합니다.
- 기존 TFS 브랜치는 이름을 일괄 변경하지 않습니다.

예시:

```text
GitHub Issue #4
[MUBLE-4] [FEAT] 소셜 로그인 구현

GitHub PR #5
[MUBLE-4] [FEAT] 소셜 로그인 구현

GitHub Issue #6
[MUBLE-5] [FIX] 로그인 오류 수정

GitHub PR #7
[MUBLE-5] [FIX] 로그인 오류 수정
```

## 브랜치 이름

```text
{type}/MUBLE-{ticket-number}
```

허용되는 type은 다음과 같습니다.

- `feat`: 새로운 기능
- `fix`: 버그 및 오류 수정
- `hotfix`: 운영 또는 배포 긴급 수정
- `chore`: 설정, 의존성, 패키지 구성 등 작은 변경
- `delete`: 불필요한 코드나 파일 삭제
- `docs`: 문서 및 주석 변경
- `refactor`: 기능 변경 없는 코드 구조 개선

예시:

```text
feat/MUBLE-4
fix/MUBLE-5
chore/MUBLE-7
```

기존 `{type}/TFS-{ticket-number}` 브랜치는 호환성을 위해 허용합니다.

## Issue

Issue 제목은 다음 형식을 사용합니다.

```text
[MUBLE-작업번호] [TYPE] 작업 요약
```

Issue 양식에서 TYPE을 선택하여 작성하면 GitHub Actions가 MUBLE 작업 번호를 자동으로 추가합니다.

MUBLE 작업 번호는 기존 번호의 최댓값 다음부터 발급합니다.

## Pull Request

PR 제목은 연결된 Issue 제목과 동일하게 설정합니다.

```text
[MUBLE-작업번호] [TYPE] 작업 요약
```

PR 본문에는 실제 GitHub Issue 번호를 사용하여 다음 문구를 작성합니다.

```text
Closes #6
```

GitHub Actions는 연결된 Issue를 조회하여 PR 제목을 자동으로 수정합니다.

브랜치 번호는 GitHub Issue 번호가 아닌 MUBLE 작업 번호와 일치해야 합니다.

모든 일반 작업 PR은 `dev`를 대상으로 생성합니다.

`main`에는 원칙적으로 `dev`만 병합하며, 긴급 수정은 별도의 hotfix 규칙을 따릅니다.
