package team.themoment.readygsmserver.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;
import team.themoment.readygsmserver.global.discord.DiscordNotificationService;

import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final DiscordNotificationService discordNotificationService;

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[ASYNC-UNHANDLED] {}.{}", method.getDeclaringClass().getSimpleName(), method.getName(), ex);
        discordNotificationService.sendServerError(
                "비동기 서버 오류 발생",
                ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName(),
                "ASYNC",
                method.getDeclaringClass().getSimpleName() + "." + method.getName(),
                null,
                Thread.currentThread().getName(),
                ex);
    }
}
