package com.back.domain.post.post.repository;

import com.back.domain.post.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// 글 저장/조회/삭제를 담당. 인터페이스만 만들면 스프링이 구현체를 자동으로 만들어 준다
// JpaRepository<엔티티, id 타입>
// 댓글은 글을 통해서만 추가/삭제하므로 PostCommentRepository는 만들지 않는다
public interface PostRepository extends JpaRepository<Post, Integer> {
}
