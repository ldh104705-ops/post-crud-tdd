package com.back.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;

// 조회가 아닌 요청(작성/삭제 등)의 공통 응답 모양 (RsData = Result Data, 강의 13~19강)
// resultCode: "HTTP응답코드-세부코드" 형식. 예) "201-1" = 생성 성공(201) 중 1번 상황
// statusCode: resultCode의 앞자리 숫자(201). ResponseAspect가 이 값으로 HTTP 응답코드를 정한다
//             JSON에는 resultCode와 같은 정보라 중복이므로 @JsonIgnore로 숨긴다 (강의 28·31강)
// msg: 사람이 읽을 결과 메시지
// data: 함께 돌려줄 데이터
//
// <T>: 제네릭(타입 매개변수). data의 타입을 쓰는 쪽에서 정한다 (강의 18강)
//   RsData<PostDto>        → data는 글
//   RsData<PostCommentDto> → data는 댓글
//   RsData<Void>           → data 없음 (강의 19강)
// 예전처럼 data를 PostDto로 고정하면 댓글을 담을 수 없고, Object로 하면 무엇이 들어있는지 알 수 없다
public record RsData<T>(
        String resultCode,
        @JsonIgnore int statusCode,
        String msg,
        T data
) {
    // 데이터가 없을 때 쓰는 생성자 → data는 null
    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }

    // statusCode는 직접 넣지 않고 resultCode에서 뽑아낸다
    // "201-1".split("-", 2) → ["201", "1"] → 201
    public RsData(String resultCode, String msg, T data) {
        this(resultCode, Integer.parseInt(resultCode.split("-", 2)[0]), msg, data);
    }
}
