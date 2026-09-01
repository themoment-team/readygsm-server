package team.themoment.readygsmserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import team.themoment.sdk.exception.ExpectedException;

import java.time.Duration;

/**
 * IP 단위 요청 제한.
 *
 * <p>엔드포인트가 공개되어 있어 제한이 없으면 사실상 무료 AI API가 된다.
 * 인증이 없으므로 세션 단위로 세면 세션을 새로 발급받아 우회할 수 있어 IP를 기준으로 센다.
 */
@Component
@RequiredArgsConstructor
public class ChatRateLimiter {

    private static final String ASK_KEY_PREFIX = "chat:rate:ask:";
    private static final String SESSION_KEY_PREFIX = "chat:rate:session:";
    private static final Duration WINDOW = Duration.ofMinutes(1);

    /** AI를 호출하므로 비용이 든다. */
    private static final long MAX_ASK_PER_WINDOW = 10L;

    /** 세션 발급 자체는 싸지만 무제한이면 Redis가 채워진다. */
    private static final long MAX_SESSION_PER_WINDOW = 20L;

    private final StringRedisTemplate stringRedisTemplate;

    public void validateAsk(String clientIp) {
        validate(ASK_KEY_PREFIX + clientIp, MAX_ASK_PER_WINDOW);
    }

    public void validateSessionCreation(String clientIp) {
        validate(SESSION_KEY_PREFIX + clientIp, MAX_SESSION_PER_WINDOW);
    }

    private void validate(String key, long maxRequests) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count == null) {
            return;
        }
        if (count == 1L) {
            stringRedisTemplate.expire(key, WINDOW);
        }
        if (count > maxRequests) {
            throw new ExpectedException("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}
