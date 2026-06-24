package team.themoment.readygsmserver.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.themoment.readygsmserver.global.discord.DiscordNotificationService;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.sdk.response.CommonApiResponse;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ServerErrorExceptionHandler {

    private final DiscordNotificationService discordNotificationService;

    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity<CommonApiResponse<?>> handleExpectedException(
            ExpectedException e, HttpServletRequest request) {
        HttpStatus status = e.getStatusCode();
        if (status.is5xxServerError()) {
            log.error("[EXPECTED-5XX] {} {}", request.getMethod(), request.getRequestURI(), e);
            discordNotificationService.sendServerError(
                    "서버 오류 발생",
                    e.getMessage() != null ? e.getMessage() : "An error occurred",
                    request.getMethod(),
                    request.getRequestURI(),
                    getClientIp(request),
                    Thread.currentThread().getName(),
                    e);
        }
        String message = e.getMessage() != null ? e.getMessage() : "An error occurred";
        return ResponseEntity.status(status)
                .body(CommonApiResponse.error(message, status));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<?>> handleUnexpectedException(
            Exception e, HttpServletRequest request) {
        log.error("[UNHANDLED-EXCEPTION] {} {}", request.getMethod(), request.getRequestURI(), e);
        discordNotificationService.sendServerError(
                "예상치 못한 서버 오류 발생",
                e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(),
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request),
                Thread.currentThread().getName(),
                e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonApiResponse.error("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) return ip.trim();
        return request.getRemoteAddr();
    }
}