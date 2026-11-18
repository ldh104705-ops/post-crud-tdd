package com.back.global.initData;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 앱이 켜질 때 BaseInitData가 샘플 데이터를 제대로 만들었는지 확인
@ActiveProfiles("test")
@SpringBootTest
@Transactional // 테스트가 끝나면 롤백 + 글의 댓글 목록(LAZY)을 읽을 수 있게 트랜잭션 유지
public class BaseInitDataTest {
    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("샘플 데이터: 글 3건, 댓글 5건")
    void t1() {
        List<Post> posts = postRepository.findAll();

        // 글 3건
        assertThat(posts).hasSize(3);

        // 댓글 5건 (1번 글에 3건, 2번 글에 2건, 3번 글에 0건)
        assertThat(posts.get(0).getComments()).hasSize(3);
        assertThat(posts.get(1).getComments()).hasSize(2);
        assertThat(posts.get(2).getComments()).isEmpty();

        int totalComments = posts.stream()
                .mapToInt(post -> post.getComments().size())
                .sum();
        assertThat(totalComments).isEqualTo(5);
    }
}
