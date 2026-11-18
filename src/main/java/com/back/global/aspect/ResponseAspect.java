package com.back.global.aspect;

import com.back.global.rsData.RsData;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// AOP(관점 지향 프로그래밍): 여러 메서드에 공통으로 필요한 일을 한 곳에 모아, 메서드 앞뒤에 끼워 넣는다
// 여기서는 "RsData를 돌려주는 컨트롤러 액션"이 끝난 뒤, resultCode 앞자리로 HTTP 응답코드를 맞춘다
// 액션마다 ResponseEntity로 상태코드를 직접 지정하는 대신 이 클래스 하나로 해결 (강의 27·28·37강)
@Aspect
@Component
public class ResponseAspect {
    // 현재 요청에 대한 응답 객체 (스프링이 요청마다 알맞은 객체를 연결해 준다)
    private final HttpServletResponse response;

    public ResponseAspect(HttpServletResponse response) {
        this.response = response;
    }

    // @Around: 대상 메서드를 감싸서, 실행 전후에 코드를 넣는다
    // 대상(포인트컷): RsData를 반환하는 public 메서드 중에서
    //               @Controller/@RestController 클래스에 있고, @GetMapping 같은 매핑 어노테이션이 붙은 것
    @Around("""
                execution(public com.back.global.rsData.RsData *(..)) &&
                (
                    within(@org.springframework.stereotype.Controller *) ||
                    within(@org.springframework.web.bind.annotation.RestController *)
                ) &&
                (
                    @annotation(org.springframework.web.bind.annotation.GetMapping) ||
                    @annotation(org.springframework.web.bind.annotation.PostMapping) ||
                    @annotation(org.springframework.web.bind.annotation.PutMapping) ||
                    @annotation(org.springframework.web.bind.annotation.DeleteMapping) ||
                    @annotation(org.springframework.web.bind.annotation.RequestMapping)
                )
            """)
    public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        // 원래 액션 메서드 실행
        Object proceed = joinPoint.proceed();

        // 결과(RsData)의 statusCode를 HTTP 응답코드로 설정
        RsData<?> rsData = (RsData<?>) proceed;
        response.setStatus(rsData.statusCode());

        return proceed;
    }
}
