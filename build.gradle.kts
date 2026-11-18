// build.gradle.kts : 빌드 설명서 (어떤 라이브러리를 쓰고, Java 몇 버전으로 빌드할지)

plugins {
    java
    // 스프링부트 버전
    id("org.springframework.boot") version "4.1.2"
    // 라이브러리 버전을 스프링부트에 맞춰 자동으로 정해 주는 플러그인
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com"
version = "0.0.1-SNAPSHOT"
description = "post-crud-tdd"

java {
    toolchain {
        // 이 프로젝트는 Java 25로 빌드한다
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    // 웹 기능 (컨트롤러, 내장 톰캣). Boot 4에서 starter-web → starter-webmvc로 이름이 바뀜
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    // JPA: 자바 객체(엔티티)를 DB 테이블에 저장/조회
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Validation: @NotBlank, @Size 같은 어노테이션으로 입력값 검사
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Database
    // H2: 설치 없이 쓰는 가벼운 DB (개발은 파일, 테스트는 메모리)
    runtimeOnly("com.h2database:h2")
    // H2 콘솔: 브라우저에서 DB 내용을 보는 화면 (Boot 4에서 별도 모듈로 분리됨)
    implementation("org.springframework.boot:spring-boot-h2console")

    // Lombok: @Getter 같은 어노테이션으로 반복 코드를 자동 생성
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Development: 코드를 고치면 서버를 자동 재시작
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    // 테스트는 JUnit 5로 실행
    useJUnitPlatform()
}
