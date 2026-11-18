package com.back.global.jpa.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

// 모든 엔티티가 공통으로 가지는 필드(id, 생성일, 수정일)를 모아 둔 부모 클래스
// @MappedSuperclass: 이 클래스 자체는 테이블이 되지 않고, 필드만 자식 엔티티의 테이블에 들어간다
// @EntityListeners: 저장/수정될 때 아래 날짜 필드를 자동으로 채워 준다 (BackApplication의 @EnableJpaAuditing 필요)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    // 기본키. IDENTITY = DB가 1, 2, 3... 순서로 번호를 매긴다 (저장 전에는 0)
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    // 처음 저장될 때 자동 기록
    @CreatedDate
    private LocalDateTime createDate;

    // 수정될 때마다 자동 기록
    @LastModifiedDate
    private LocalDateTime modifyDate;

    // 같은 클래스이고 id가 같으면 같은 엔티티로 본다
    // → 나중에 comments.remove(댓글)처럼 리스트에서 엔티티를 찾아 지울 때 쓰인다
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
