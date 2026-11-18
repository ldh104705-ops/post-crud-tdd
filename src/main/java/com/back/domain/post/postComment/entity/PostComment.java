package com.back.domain.post.postComment.entity;

import com.back.domain.post.post.entity.Post;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

// 댓글 엔티티 → post_comment 테이블
@Entity
@Getter
@NoArgsConstructor
public class PostComment extends BaseEntity {
    // 이 댓글이 달린 글 (post_comment 테이블의 post_id 컬럼)
    // LAZY: 댓글을 조회할 때 글은 바로 가져오지 않고, 실제로 쓸 때 가져온다
    @ManyToOne(fetch = LAZY)
    private Post post;
    private String content;

    public PostComment(Post post, String content) {
        this.post = post;
        this.content = content;
    }

    // 댓글 수정 (더티 체킹으로 UPDATE)
    public void modify(String content) {
        this.content = content;
    }
}
