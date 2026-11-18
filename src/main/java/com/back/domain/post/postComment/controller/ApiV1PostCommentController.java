package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 댓글 API. 댓글은 글에 딸린 자원이므로 주소도 글 아래에 둔다: /api/v1/posts/{글 번호}/comments (강의 10강)
// TDD: 테스트(ApiV1PostCommentControllerTest)를 먼저 쓰고, 그 테스트를 통과시키는 만큼만 구현한다
@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostService postService;
}
