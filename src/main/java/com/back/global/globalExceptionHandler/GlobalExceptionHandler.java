package com.back.global.globalExceptionHandler;

import com.back.global.rsData.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

// 모든 컨트롤러에서 던져진 예외를 한 곳에서 받아, 정해진 모양(RsData)으로 응답한다 (강의 48~52강)
// 이게 없으면 스프링 기본 에러 JSON(timestamp, status, error, path)이 나간다
@ControllerAdvice
public class GlobalExceptionHandler {

    // 없는 데이터를 찾으면: 서비스가 돌려준 빈 Optional에 컨트롤러가 .get()을 부르면 NoSuchElementException이 던져진다
    // 이것을 404(찾을 수 없음)로 바꾼다 (강의 48강)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RsData<Void>> handle(NoSuchElementException ex) {
        return new ResponseEntity<>(
                new RsData<>("404-1", "해당 데이터가 존재하지 않습니다."),
                NOT_FOUND
        );
    }

    // @Valid 검증에 실패하면 컨트롤러 메서드가 실행되지 않고 MethodArgumentNotValidException이 던져진다
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handle(MethodArgumentNotValidException ex) {
        // 실패한 항목들을 "필드-검증종류-메시지" 형태로 만들고, 순서가 매번 같도록 정렬해서 줄바꿈으로 잇는다
        // 예) title-NotBlank-must not be blank
        String msg = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .map(error -> error.getField() + "-" + error.getCode() + "-" + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("\n"));

        // 예외 처리기는 ResponseAspect 대상이 아니므로 ResponseEntity로 HTTP 400을 직접 지정한다
        return new ResponseEntity<>(
                new RsData<>("400-1", msg),
                BAD_REQUEST
        );
    }
}
