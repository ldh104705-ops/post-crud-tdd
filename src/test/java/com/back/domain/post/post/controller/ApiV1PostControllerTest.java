package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import jakarta.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 글 API 테스트: MockMvc로 가짜 HTTP 요청을 보내 응답을 검사한다 (강의 33강)
// 구조: 요청 → 검증 2단계 (강의 35강)
@ActiveProfiles("test")  // 메모리 DB
@SpringBootTest          // 앱 전체(빈, DB, 샘플 데이터)를 띄운다
@AutoConfigureMockMvc    // MockMvc 준비
@Transactional           // 테스트마다 롤백 → 항상 샘플 데이터(글 3, 댓글 5)에서 시작
public class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;
    // 엔티티를 직접 조회할 때 쓰는 JPA 핵심 객체 (댓글 리포지터리가 없어서 댓글 수를 셀 때 사용)
    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("글 다건조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts")
                )
                .andDo(print());

        List<Post> posts = postService.findAll();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk())
                // 조회는 RsData로 감싸지 않고 데이터(배열)를 그대로 응답 (강의 14강: 조회까지 감싸는 건 오버스펙)
                .andExpect(jsonPath("$.length()").value(posts.size())); // 강의 47강

        // 배열의 각 글을 DB의 글과 하나씩 비교 (강의 45강)
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
                    // 날짜는 JSON과 자바의 소수점 자릿수가 다를 수 있어 앞 20글자만 비교
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
        }
    }

    @Test
    @DisplayName("글 단건조회")
    void t2() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/" + id)
                )
                .andDo(print());

        Post post = postService.findById(id).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                // 응답의 모든 필드를 DB의 글과 비교 (강의 44강)
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.createDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.modifyDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()));
    }

    @Test
    @DisplayName("글 단건조회, 없는 글이면 404")
    void t3() throws Exception {
        int id = Integer.MAX_VALUE; // 샘플 데이터에 없는 번호

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/" + id)
                )
                .andDo(print());

        // 조회 성공은 데이터 그대로, 조회 실패는 RsData로 응답 (강의 14·48강)
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("404-1"))
                .andExpect(jsonPath("$.msg").value("해당 데이터가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("글 작성")
    void t4() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                )
                .andDo(print());

        // 방금 작성된 글 = 가장 최근 글 (강의 38강)
        Post post = postService.findLatest().get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                // 생성 성공은 201 (ResponseAspect가 resultCode 앞자리로 설정)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(post.getId())))
                // data의 모든 필드 (강의 39강)
                .andExpect(jsonPath("$.data.id").value(post.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(post.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(post.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.title").value("제목 new"))
                .andExpect(jsonPath("$.data.content").value("내용 new"));

        // 글이 하나 늘었다 (샘플 3건 → 4건)
        assertThat(postService.count()).isEqualTo(4);
    }

    @Test
    @DisplayName("글 작성, 제목 없이 요청하면 400")
    void t5() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "",
                                            "content": "내용 new"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                // 빈 제목은 @NotBlank와 @Size(min = 2) 둘 다 실패
                // 메시지 문장은 PC 언어 설정(영어/한글)에 따라 달라서 "필드-검증종류" 부분만 확인 (강의 50강)
                .andExpect(jsonPath("$.msg").value(Matchers.containsString("title-NotBlank-")))
                .andExpect(jsonPath("$.msg").value(Matchers.containsString("title-Size-")));

        // 실패했으므로 글이 늘지 않았다
        assertThat(postService.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("글 수정")
    void t6() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "제목 new",
                                            "content": "내용 new"
                                        }
                                        """)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 수정되었습니다.".formatted(id)));

        // 응답만 보지 않고 DB의 글이 실제로 바뀌었는지 확인
        Post post = postService.findById(id).get();
        assertThat(post.getTitle()).isEqualTo("제목 new");
        assertThat(post.getContent()).isEqualTo("내용 new");
    }

    @Test
    @DisplayName("글 삭제")
    void t7() throws Exception {
        int id = 1;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/" + id)
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 삭제되었습니다.".formatted(id)));

        // DB에서 실제로 지워졌는지 (강의 lab2 방식)
        assertThat(postService.findById(id)).isEmpty();

        // 1번 글의 댓글 3건도 cascade REMOVE로 함께 지워져서 댓글은 5건 → 2건
        // JPQL: 테이블이 아니라 엔티티(PostComment) 기준 쿼리. 실행 전에 미뤄 둔 DELETE가 먼저 실행된다
        long commentCount = em
                .createQuery("select count(pc) from PostComment pc", Long.class)
                .getSingleResult();
        assertThat(commentCount).isEqualTo(2);
    }
}
