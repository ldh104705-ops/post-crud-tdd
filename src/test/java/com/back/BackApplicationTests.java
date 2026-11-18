package com.back;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// test 프로파일 → application-test.yaml(메모리 DB)을 사용
@ActiveProfiles("test")
@SpringBootTest
class BackApplicationTests {

    // 앱이 오류 없이 켜지는지만 확인하는 테스트
    @Test
    void contextLoads() {
    }

}
