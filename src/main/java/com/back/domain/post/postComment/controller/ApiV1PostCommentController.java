package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 댓글 API. 댓글은 글에 딸린 자원이므로 주소도 글 아래에 둔다: /api/v1/posts/{글 번호}/comments (강의 10강)
// TDD: 테스트(ApiV1PostCommentControllerTest)를 먼저 쓰고, 그 테스트를 통과시키는 만큼만 구현한다
// 댓글 리포지터리/서비스 없이 글(Post)을 통해 댓글을 다룬다
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;

    // 댓글 다건조회: GET /api/v1/posts/{postId}/comments (강의 10강)
    @GetMapping
    @Transactional(readOnly = true)
    public List<PostCommentDto> getItems(@PathVariable int postId) {
        // 없는 글이면 .get()에서 예외 → 404-1
        Post post = postService.findById(postId).get();

        // 글의 댓글 목록(LAZY)을 트랜잭션 안에서 읽어 DTO로 바꾼다
        return post
                .getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }

    // 댓글 단건조회: GET /api/v1/posts/{postId}/comments/{id} (강의 10강)
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public PostCommentDto getItem(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Post post = postService.findById(postId).get();

        // 그 글의 댓글 중에서만 찾는다 → 다른 글의 댓글 번호면 빈 Optional → .get()에서 예외 → 404-1
        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }

    // 요청 본문: { "content": "..." }
    record PostCommentWriteReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String content
    ) {
    }

    // 댓글 작성: POST /api/v1/posts/{postId}/comments (강의 56강)
    @PostMapping
    @Transactional
    public RsData<PostCommentDto> write(
            @PathVariable int postId,
            @RequestBody @Valid PostCommentWriteReqBody reqBody
    ) {
        Post post = postService.findById(postId).get();

        PostComment postComment = postService.writeComment(post, reqBody.content());

        // 댓글은 리스트에 추가만 된 상태라 INSERT 전 → id가 0, 날짜가 null
        // flush로 INSERT를 지금 실행해서 DB가 매긴 번호와 날짜를 받아 온다 (강의 56강)
        postService.flush();

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다.".formatted(postComment.getId()),
                new PostCommentDto(postComment)
        );
    }

    record PostCommentModifyReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String content
    ) {
    }

    // 댓글 수정: PUT /api/v1/posts/{postId}/comments/{id} (강의 32강)
    @PutMapping("/{id}")
    @Transactional
    public RsData<Void> modify(
            @PathVariable int postId,
            @PathVariable int id,
            @RequestBody @Valid PostCommentModifyReqBody reqBody
    ) {
        Post post = postService.findById(postId).get();

        PostComment postComment = post.findCommentById(id).get();

        postService.modifyComment(postComment, reqBody.content());

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(id)
        );
    }
}
