package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 글 API: /api/v1/posts (/api/v1: 버전을 붙여 나중에 v2를 만들어도 기존 클라이언트가 깨지지 않게, 강의 6강)
// TDD: 테스트(ApiV1PostControllerTest)를 먼저 쓰고, 그 테스트를 통과시키는 만큼만 구현한다
// @RestController = @Controller + @ResponseBody → 돌려준 객체를 JSON으로 응답
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    // 글 다건조회: GET /api/v1/posts (강의 4·9강)
    // readOnly = true: 조회만 하는 트랜잭션 (변경 감지를 하지 않아 가볍다, 강의 11강)
    @GetMapping
    @Transactional(readOnly = true)
    public List<PostDto> getItems() {
        List<Post> items = postService.findAll();

        // 엔티티 목록 → DTO 목록. PostDto::new = post -> new PostDto(post) (강의 9강)
        return items
                .stream()
                .map(PostDto::new)
                .toList();
    }

    // 글 단건조회: GET /api/v1/posts/{id} (강의 5강)
    // @PathVariable: 주소의 {id} 부분을 int id로 받는다
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PostDto getItem(@PathVariable int id) {
        // 없는 글이면 .get()에서 NoSuchElementException → GlobalExceptionHandler가 404-1로 응답
        Post post = postService.findById(id).get();

        return new PostDto(post);
    }

    // 요청 본문(JSON)을 받을 객체. 이 컨트롤러에서만 쓰므로 안쪽에 둔다 (강의 22·24강)
    record PostWriteReqBody(
            // @NotBlank: null, "", "   " 모두 안 됨 / @Size: 글자 수 범위
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    // 글 작성: POST /api/v1/posts (강의 22강)
    // @RequestBody: 요청 본문 JSON → PostWriteReqBody
    // @Valid: 검증 실패 시 메서드가 실행되지 않고 MethodArgumentNotValidException → 400-1
    @PostMapping
    @Transactional
    public RsData<PostDto> write(@RequestBody @Valid PostWriteReqBody reqBody) {
        Post post = postService.write(reqBody.title(), reqBody.content());

        // 201-1 → ResponseAspect가 HTTP 201로 설정
        return new RsData<>(
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post)
        );
    }

    // 수정 요청 본문 (작성과 규칙은 같지만, 나중에 달라질 수 있어 따로 둔다, 강의 31강)
    record PostModifyReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    // 글 수정: PUT /api/v1/posts/{id} (강의 31강)
    // 주소는 대상(몇 번 글), 행동(수정)은 HTTP 메서드(PUT)로 나타낸다
    @PutMapping("/{id}")
    @Transactional
    public RsData<Void> modify(
            @PathVariable int id,
            @RequestBody @Valid PostModifyReqBody reqBody
    ) {
        Post post = postService.findById(id).get();

        postService.modify(post, reqBody.title(), reqBody.content());

        // 돌려줄 데이터가 없으므로 RsData<Void> (강의 19강)
        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(id)
        );
    }

    // 글 삭제: DELETE /api/v1/posts/{id} (강의 16·21강)
    // GET으로 삭제하면 브라우저 미리 불러오기나 검색엔진 방문만으로 지워질 수 있어, 삭제는 DELETE 메서드로
    @DeleteMapping("/{id}")
    @Transactional
    public RsData<Void> delete(@PathVariable int id) {
        Post post = postService.findById(id).get();

        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글이 삭제되었습니다.".formatted(id)
        );
    }
}
