package com.back.domain.post.postComment.dto;

import com.back.domain.post.postComment.entity.PostComment;

import java.time.LocalDateTime;

// 응답용 댓글 데이터 (PostDto와 같은 이유로 엔티티 대신 사용, 강의 10강)
// 댓글의 post 필드는 넣지 않는다 → 댓글 → 글 → 댓글 ... 순환이 생기지 않는다
public record PostCommentDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String content
) {
    public PostCommentDto(PostComment postComment) {
        this(
                postComment.getId(),
                postComment.getCreateDate(),
                postComment.getModifyDate(),
                postComment.getContent()
        );
    }
}
