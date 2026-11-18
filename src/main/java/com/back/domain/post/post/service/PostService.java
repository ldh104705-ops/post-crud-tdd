package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
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

    // 글 수정: 값만 바꾸면 트랜잭션이 끝날 때 JPA가 변경을 감지해 UPDATE (더티 체킹)
    public void modify(Post post, String title, String content) {
        post.modify(title, content);
    }

    // 글 삭제. 댓글은 cascade REMOVE로 함께 삭제된다
    public void delete(Post post) {
        postRepository.delete(post);
    }

    // 댓글 작성: 글의 댓글 목록에 추가 (저장은 cascade PERSIST가 한다)
    public PostComment writeComment(Post post, String content) {
        return post.addComment(content);
    }

    // 지금까지 모아 둔 변경(INSERT 등)을 DB에 바로 보낸다. 커밋은 아니다 (강의 56강)
    public void flush() {
        postRepository.flush();
    }

    // 가장 최근 글 (테스트에서 "방금 작성한 글"을 가져올 때 사용)
    public Optional<Post> findLatest() {
        return postRepository.findFirstByOrderByIdDesc();
    }
}
