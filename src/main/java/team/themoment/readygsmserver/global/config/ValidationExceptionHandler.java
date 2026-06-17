package team.themoment.readygsmserver.global.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.themoment.sdk.response.CommonApiResponse;

import java.util.stream.Collectors;

/**
 * Bean Validation(@Valid / @Validated) 실패 시 발생하는 예외를 처리한다.
 *
 * <p>the-sdk의 GlobalExceptionHandler는 ExpectedException만 처리하므로,
 * 검증 실패 예외는 잡히지 않아 본문 없는 400만 응답된다. 이 핸들러가 실패한 필드별
 * 메시지를 모아 SDK 표준 포맷({@link CommonApiResponse#error})으로 응답한다.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(this::formatError)
                .collect(Collectors.joining(", "));
        return CommonApiResponse.error(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public CommonApiResponse<?> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(this::formatViolation)
                .collect(Collectors.joining(", "));
        return CommonApiResponse.error(message, HttpStatus.BAD_REQUEST);
    }

    private String formatError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return error.getDefaultMessage();
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
