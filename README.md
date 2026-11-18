# post-crud-tdd

글·댓글 CRUD REST API를 TDD(테스트 주도 개발)로 만드는 부트캠프 과제입니다. 장희성 강사의 "스프링부트 REST API" 강의(14172) 방식을 따릅니다.

- **진행 방식**: 기능마다 **테스트를 먼저 작성해 실패를 확인(빨강) → 테스트를 통과하도록 구현(초록)**을 각각 커밋으로 남깁니다.
- **커밋 번호 = 태그**: 커밋마다 `001`, `002`, … 태그를 붙여, 태그 링크로 그 시점의 코드를 볼 수 있습니다.

## 과제 요구사항

- 프로젝트를 제로에서 시작
- 샘플 데이터 글 3개, 댓글 5개
- 글 CRUD 엔드포인트 구현 by TDD (다건조회, 단건조회)
- 댓글 CRUD 엔드포인트 구현 by TDD (다건조회, 단건조회)
- 커밋 메시지를 자세히 작성
- 각 커밋마다 001, 002 같이 태그

## 참고 자료

| 자료 | 링크 |
|---|---|
| 14172 스프링부트 REST API | [강의](https://www.slog.gg/p/14172/raw) / [GitHub](https://github.com/jhs512/p-14172-2) |

## 기술 스택

- Java 25, Spring Boot 4.1.2, Gradle (Kotlin DSL)
- Spring Data JPA, Validation, H2 (개발: 파일 DB, 테스트: 메모리 DB)

## 작업내역

### 001 저장소 준비
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/001)
- 작업: git 저장소 초기화(main 브랜치), .gitignore, .gitattributes, README 작성
- 이유: 모든 작업을 작은 커밋과 태그로 기록해서, 나중에 커밋 하나씩 따라가며 이해하기 위함
- 결과: 첫 커밋 생성, GitHub 공개 저장소에 업로드
