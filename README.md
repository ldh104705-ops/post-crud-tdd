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

### 005 공통 응답 RsData, ResponseAspect, 전역 예외 처리
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/005)
- 강의: 14172 13~19강(RsData), 27·28·31·37강(ResponseAspect), 48·50강(GlobalExceptionHandler)
- 작업: 공통 응답 `RsData<T>`(resultCode, msg, data), resultCode 앞자리를 HTTP 응답코드로 바꿔 주는 ResponseAspect, 없는 데이터(404-1)와 검증 실패(400-1)를 RsData로 바꾸는 GlobalExceptionHandler
- 이유: 이후 CRUD 기능이 모두 같은 응답 모양과 같은 실패 처리를 쓰도록 공통 기반을 먼저 마련. 변천 과정(PostDto 전용 RsData → 제네릭, ResponseEntity → AOP)은 어제 과제(post-api-plus)에서 단계별로 다뤘으므로 최종 형태로 가져옴
- 결과: 컴파일과 기존 테스트 통과 (아직 이 기반을 쓰는 API는 없음)

### 006 글 다건조회, 테스트 먼저 작성 (실패)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/006)
- 강의: 14172 33·35·45·47강
- 작업: ApiV1PostControllerTest에 글 다건조회 테스트(t1). 컴파일되도록 기능 없는 ApiV1PostController와, 테스트가 DB 값과 비교할 때 쓸 PostService.findAll·findById 추가
- 이유: TDD. "GET /api/v1/posts는 글 3개를 배열로, 각 글의 모든 필드를 담아 응답해야 한다"를 테스트로 먼저 정함
- 결과: **실패(빨강)**. 요청을 처리할 컨트롤러 메서드가 아직 없음

### 007 글 다건조회, 구현 (통과)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/007)
- 강의: 14172 4·8·9·11강
- 작업: 응답용 PostDto(record), `GET /api/v1/posts` → 모든 글을 PostDto 목록으로 응답(@Transactional(readOnly = true))
- 이유: 006 테스트 통과. 엔티티를 그대로 내보내면 글↔댓글 순환참조와 API 모양이 엔티티에 묶이는 문제가 있어 DTO 사용
- 결과: **통과(초록)**. 서버에서 글 3개 배열 응답 확인

### 008 글 단건조회, 테스트 먼저 작성 (실패)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/008)
- 강의: 14172 44·48강
- 작업: 글 단건조회 테스트(t2: 1번 글의 모든 필드), 없는 글 단건조회 테스트(t3: 404, `404-1`)
- 이유: TDD. 성공은 데이터 그대로, 실패(없는 글)는 RsData로 응답해야 한다는 것을 테스트로 먼저 정함
- 결과: **실패(빨강)**. 단건조회 테스트 2개 실패, 기존 테스트 3개 통과

### 009 글 단건조회, 구현 (통과)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/009)
- 강의: 14172 5·48강
- 작업: `GET /api/v1/posts/{id}` → PostDto 응답. 없는 글은 `findById(id).get()`의 예외를 GlobalExceptionHandler가 404로 바꿈
- 이유: 008 테스트 통과
- 결과: **통과(초록)**. 서버에서 1번 글 조회 200, 100번 글 조회 404 `404-1` 확인

### 010 글 작성, 테스트 먼저 작성 (실패)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/010)
- 강의: 14172 33·38·39·50강
- 작업: 글 작성 테스트(t4: 201, `201-1`, data의 모든 필드, 글 수 4건), 제목 없이 작성 테스트(t5: 400, `400-1`, 글 수 3건 그대로). 테스트에서 방금 쓴 글을 찾기 위한 PostService.findLatest(가장 최근 글) 추가
- 이유: TDD. 작성 성공은 201과 작성된 글, 검증 실패는 400을 응답해야 한다는 것을 테스트로 먼저 정함
- 결과: **실패(빨강)**. 작성 테스트 2개 실패(POST를 처리할 메서드 없음), 기존 5개 통과

### 011 글 작성, 구현 (통과)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/011)
- 강의: 14172 22·24·28강
- 작업: `POST /api/v1/posts`. 요청은 컨트롤러 내부 record PostWriteReqBody(@NotBlank, @Size)로 받고 @Valid로 검증, 응답은 `RsData<PostDto>` `201-1`
- 이유: 010 테스트 통과
- 결과: **통과(초록)**. 서버에서 작성 201, 빈 제목 400 확인

### 012 글 수정, 테스트 먼저 작성 (실패)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/012)
- 강의: 14172 31·36·41강
- 작업: 글 수정 테스트(t6: `PUT /api/v1/posts/1` → 200, `200-1`, DB의 제목·내용이 바뀌었는지 확인)
- 이유: TDD. 수정은 PUT 메서드, 응답은 데이터 없는 RsData, 실제 DB 반영까지 테스트로 먼저 정함
- 결과: **실패(빨강)**. 수정 테스트 실패(PUT을 처리할 메서드 없음), 기존 7개 통과

### 013 글 수정, 구현 (통과)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/013)
- 강의: 14172 19·31강
- 작업: `PUT /api/v1/posts/{id}`. PostModifyReqBody(@Valid)로 받고 post.modify로 값만 바꿈(더티 체킹으로 UPDATE), 응답은 `RsData<Void>` `200-1`
- 이유: 012 테스트 통과
- 결과: **통과(초록)**. 서버에서 1번 글 수정 후 조회 시 제목·내용이 바뀌고 modifyDate가 갱신됨

### 014 글 삭제, 테스트 먼저 작성 (실패)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/014)
- 강의: 14172 42강, lab2 브랜치(DB 반영 확인)
- 작업: 글 삭제 테스트(t7: `DELETE /api/v1/posts/1` → 200, `200-1`, DB에서 1번 글이 사라지고 댓글이 5건 → 2건)
- 이유: TDD. 삭제 응답뿐 아니라 실제 삭제와 연관 댓글 삭제(cascade)까지 테스트로 먼저 정함
- 결과: **실패(빨강)**. 삭제 테스트 실패(DELETE를 처리할 메서드 없음), 기존 8개 통과

### 015 글 삭제, 구현 (통과)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/015)
- 강의: 14172 16·21강
- 작업: PostService.delete, `DELETE /api/v1/posts/{id}` → `RsData<Void>` `200-1`
- 이유: 014 테스트 통과. 댓글은 cascade REMOVE로 함께 삭제
- 결과: **통과(초록)**. 글 CRUD 테스트 7개 모두 통과. 서버에서 1번 글 삭제 시 댓글 1·2·3도 삭제, 다시 삭제하면 404

### 016 댓글 다건조회, 테스트 먼저 작성 (실패)
- 날짜: 2026-11-18
- [링크](https://github.com/ldh104705-ops/post-crud-tdd/tree/016)
- 강의: 14172 10강(댓글 주소), 54강(댓글 테스트)
- 작업: ApiV1PostCommentControllerTest에 댓글 다건조회 테스트(t1: 1번 글의 댓글 3개, 각 댓글의 모든 필드). 컴파일되도록 기능 없는 ApiV1PostCommentController(`/api/v1/posts/{postId}/comments`)
- 이유: TDD. 댓글은 글에 딸린 자원이라 주소도 글 아래에 두고, 응답은 댓글 배열이어야 한다는 것을 테스트로 먼저 정함
- 결과: **실패(빨강)**. 댓글 다건조회 실패, 기존 9개 통과
