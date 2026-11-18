package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
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
}
