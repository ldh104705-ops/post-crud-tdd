package com.back.domain.post.post.dto;

import com.back.domain.post.post.entity.Post;

import java.time.LocalDateTime;

// 응답용 글 데이터 (DTO = Data Transfer Object)
// 엔티티(Post)를 그대로 JSON으로 내보내면
//  - 글 ↔ 댓글이 서로를 가리켜 JSON 변환이 무한 반복되고 (순환참조, 강의 4강)
//  - 엔티티 필드를 바꾸면 API 응답 모양까지 바뀐다 (강의 7강)
// 그래서 내보낼 필드만 골라 담은 별도 객체를 쓴다
// record: 필드, 생성자, getter(title() 형태), equals 등을 자동으로 만들어 주는 불변 데이터 클래스 (강의 9강)
public record PostDto(
        int id,
        LocalDateTime createDate,
        LocalDateTime modifyDate,
        String title,
        String content
) {
    // 엔티티를 받아 DTO로 바꾸는 생성자
    public PostDto(Post post) {
        this(
                post.getId(),
                post.getCreateDate(),
                post.getModifyDate(),
                post.getTitle(),
                post.getContent()
        );
    }
}
