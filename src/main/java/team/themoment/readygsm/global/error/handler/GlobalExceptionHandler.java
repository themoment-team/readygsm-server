package team.themoment.readygsm.global.error.handler;

import jakarta.persistence.NonUniqueResultException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import team.themoment.readygsm.global.error.data.response.ErrorResponse;
import team.themoment.readygsm.global.error.exception.ReadyGsmException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReadyGsmException.class)
    public ResponseEntity<ErrorResponse> handleReadyGsmException(ReadyGsmException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode().getMessage(), e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<ErrorResponse> handleNonUniqueResultException(NonUniqueResultException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("요청에 대한 결과가 중복되었습니다.", HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("데이터 무결성을 위반하는 요청입니다.", HttpStatus.BAD_REQUEST.value()));
    }
}