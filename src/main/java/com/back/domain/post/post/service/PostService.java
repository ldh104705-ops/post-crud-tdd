package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// 글 관련 기능을 모아 둔 서비스
// @Transactional은 여기에 붙이지 않고, 호출하는 쪽(컨트롤러 액션, BaseInitData)에 붙인다 (강의 11강)
@Service
@RequiredArgsConstructor // final 필드를 받는 생성자를 만들어 준다 → 스프링이 PostRepository를 넣어 줌
public class PostService {
    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(String title, String content) {
        Post post = new Post(title, content);

        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // 없을 수도 있으므로 Optional로 돌려준다
    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }
}
