package com.back.domain.post.post.controller;

import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 글 API: /api/v1/posts (/api/v1: 버전을 붙여 나중에 v2를 만들어도 기존 클라이언트가 깨지지 않게, 강의 6강)
// TDD: 테스트(ApiV1PostControllerTest)를 먼저 쓰고, 그 테스트를 통과시키는 만큼만 구현한다
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;
}
