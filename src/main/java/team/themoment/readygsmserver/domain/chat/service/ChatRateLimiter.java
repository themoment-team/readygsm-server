package team.themoment.readygsmserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import team.themoment.sdk.exception.ExpectedException;

import java.time.Duration;

/**
 * 사용자 단위 요청 제한. 제한이 없으면 사실상 무료 AI API가 된다.
 *
 * <p>IP가 아니라 사용자로 센다. 학교망처럼 여러 사람이 한 공인 IP를 쓰는 환경에서는
 * IP로 세면 한 사람이 한도를 다 쓰고 나머지가 막힌다. 프록시 뒤에서 실제 IP를 알아내려면
 * 신뢰할 수 있는 헤더 설정도 따로 필요하다.
 *
 * <p>계정을 여러 개 만들어 우회할 수는 있지만 소셜 로그인 계정을 새로 만드는 비용이
 * 충분한 문턱이 된다.
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

    public void validateAsk(Long userId) {
        validate(ASK_KEY_PREFIX + userId, MAX_ASK_PER_WINDOW);
    }

    public void validateSessionCreation(Long userId) {
        validate(SESSION_KEY_PREFIX + userId, MAX_SESSION_PER_WINDOW);
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
