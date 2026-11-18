package com.back.global.initData;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

// 앱이 켜질 때 샘플 데이터(글 3건, 댓글 5건)를 넣는다
// 프로파일 제한이 없으므로 테스트(메모리 DB)에서도 똑같이 만들어진다 → 테스트는 이 데이터를 전제로 한다
@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    // 자기 자신(의 프록시). this.work1()로 부르면 @Transactional이 적용되지 않아서,
    // 스프링이 감싼 프록시를 통해 self.work1()로 부른다
    // @Lazy: 자기 자신을 주입받는 순환 참조 문제를 피하려고, 실제로 쓸 때 가져온다
    @Autowired
    @Lazy
    private BaseInitData self;
    private final PostService postService;

    // ApplicationRunner: 앱이 다 켜진 뒤 한 번 실행된다
    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        // 이미 데이터가 있으면 건너뛴다 (개발 DB는 파일이라 재시작해도 데이터가 남아 있음)
        if (postService.count() > 0) return;

        Post post1 = postService.write("제목 1", "내용 1");
        Post post2 = postService.write("제목 2", "내용 2");
        Post post3 = postService.write("제목 3", "내용 3");

        // 댓글은 글의 리스트에 넣기만 한다
        // 트랜잭션이 끝날 때 JPA가 변경을 감지하고, cascade PERSIST로 INSERT한다
        post1.addComment("댓글 1-1");
        post1.addComment("댓글 1-2");
        post1.addComment("댓글 1-3");
        post2.addComment("댓글 2-1");
        post2.addComment("댓글 2-2");
    }
}
