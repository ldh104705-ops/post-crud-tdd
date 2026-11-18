package com.back.domain.post.post.controller;

import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
