package com.back.domain.post.post.entity;

import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

// 글 엔티티 → post 테이블
// Setter는 만들지 않는다. 값은 생성자와 메서드로만 바꾼다 (어디서 바뀌는지 추적하기 쉽게)
@Entity
@Getter
@NoArgsConstructor // JPA가 DB에서 읽은 값으로 객체를 만들 때 필요한 기본 생성자
public class Post extends BaseEntity {
    private String title;
    private String content;

    // 이 글에 달린 댓글 목록 (DB 컬럼이 아니라, post_comment 테이블의 post_id로 찾아온다)
    // mappedBy = "post": 관계의 주인은 PostComment.post 필드다 (post_comment 테이블에 post_id 컬럼이 생김)
    // PERSIST: 글이 저장될 때, 리스트에 새로 추가된 댓글도 함께 저장
    // REMOVE: 글이 삭제될 때 댓글도 함께 삭제
    // orphanRemoval: 리스트에서 빠진 댓글(고아)은 DB에서도 삭제
    @OneToMany(mappedBy = "post", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<PostComment> comments = new ArrayList<>(); // null 대신 빈 리스트로 시작

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 글 수정: Setter 대신 의미가 드러나는 메서드로 값을 바꾼다
    // 트랜잭션 안에서 값만 바꾸면, 끝날 때 JPA가 변경을 감지해 UPDATE를 실행한다 (더티 체킹)
    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 댓글 추가: 댓글을 만들어 리스트에 넣는다. 저장은 PERSIST가 알아서 한다
    public PostComment addComment(String content) {
        PostComment postComment = new PostComment(this, content);
        comments.add(postComment);

        return postComment;
    }

    // 댓글 찾기: 없을 수도 있으므로 Optional로 돌려준다
    public Optional<PostComment> findCommentById(int id) {
        return comments
                .stream()
                .filter(comment -> comment.getId() == id)
                .findFirst();
    }

    // 댓글 삭제: 리스트에서 빼기만 하면 orphanRemoval이 DB에서도 지운다
    // remove()는 equals(id 비교)로 같은 댓글을 찾는다
    public boolean deleteComment(PostComment postComment) {
        if (postComment == null) return false;

        return comments.remove(postComment);
    }
}
