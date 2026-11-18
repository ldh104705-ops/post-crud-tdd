package com.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 앱의 시작점. 이 클래스가 있는 패키지(com.back)와 그 아래를 모두 스캔해서 스프링 빈으로 등록한다
@SpringBootApplication
public class BackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

}
