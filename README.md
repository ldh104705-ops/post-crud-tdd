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

### 002 프로젝트 생성, 개발/테스트 DB 분리
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/002)
- 강의: 14172 1강, 3강
- 작업: Spring Boot 4.1.2 / Java 25 / Gradle Kotlin DSL 프로젝트 생성 (webmvc, Data JPA, Validation, H2, H2 콘솔, Lombok, DevTools), dev(파일 DB) / test(메모리 DB) 프로파일 분리
- 이유: 개발 데이터는 재시작해도 남고, 테스트는 매번 깨끗한 메모리 DB에서 개발 데이터를 건드리지 않고 돌게 하기 위함
- 결과: contextLoads 테스트 통과, 서버 실행 시 db_dev.mv.db 생성, localhost:8080은 404(아직 API 없음)

### 003 엔티티 (BaseEntity, Post, PostComment)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/003)
- 강의: 14172 3강, 12강, 16강, 31·32강
- 작업: 공통 필드(id, createDate, modifyDate)를 가진 BaseEntity와 JPA Auditing, 글(Post)·댓글(PostComment) 엔티티와 1:N 연관관계(cascade PERSIST·REMOVE, orphanRemoval), 글·댓글의 수정/추가/찾기/삭제 메서드, PostRepository
- 이유: 댓글은 글에 딸린 데이터라 글을 통해서만 추가·삭제하고(댓글 리포지터리 없음), 값은 Setter 대신 의미 있는 메서드(modify 등)로만 바꾸기 위함
- 결과: 서버 실행 시 post, post_comment 테이블 생성 확인

### 004 샘플 데이터 글 3개, 댓글 5개
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/004)
- 강의: 14172 3강, 33강(테스트 프로파일·롤백)
- 작업: PostService(count, write), BaseInitData(앱 시작 시 실행, 자기 자신 프록시 `self`로 @Transactional 적용, 데이터가 있으면 건너뜀), 샘플 데이터 생성 테스트(BaseInitDataTest)
- 이유: 과제 요구사항(샘플 데이터 글 3개, 댓글 5개). 프로파일 제한이 없어 테스트 DB에도 같은 데이터가 생기므로, 이후 모든 테스트가 이 데이터를 기준으로 삼음
- 결과: 개발 DB에 글 1~3, 댓글 1~5(1번 글에 1·2·3, 2번 글에 4·5) 생성. BaseInitDataTest 통과
