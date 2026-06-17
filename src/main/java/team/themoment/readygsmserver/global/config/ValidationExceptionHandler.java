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

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    private static final String DEFAULT_MESSAGE = "올바르지 않은 입력값입니다.";

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
        String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : DEFAULT_MESSAGE;
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + message;
        }
        return message;
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        String message = violation.getMessage() != null ? violation.getMessage() : DEFAULT_MESSAGE;
        return fieldName + ": " + message;
    }
}
