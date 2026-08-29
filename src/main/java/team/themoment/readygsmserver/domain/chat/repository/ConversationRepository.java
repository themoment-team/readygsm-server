package team.themoment.readygsmserver.domain.chat.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import team.themoment.readygsmserver.domain.chat.entity.ChatMessage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 대화 기록을 Redis에 보관한다.
 *
 * <p>빈 세션도 {@code []}로 실제 저장한다. 키의 존재 여부만으로
 * 만료·위조된 세션과 아직 대화가 없는 새 세션을 구분하기 위해서다.
 *
 * <p>TTL은 슬라이딩이다. 메시지를 덧붙일 때마다 다시 채워서 대화 중에 만료되지 않게 한다.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ConversationRepository {

    private static final String KEY_PREFIX = "chat:conversation:";
    private static final Duration TTL = Duration.ofMinutes(30);

    /** 저장 상한. 프롬프트에 실제로 싣는 턴 수는 조립 단계에서 따로 자른다. */
    private static final int MAX_MESSAGES = 40;

    private static final TypeReference<List<ChatMessage>> MESSAGE_LIST_TYPE = new TypeReference<>() {
    };

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void create(String sessionId) {
        write(sessionId, List.of());
    }

    public boolean exists(String sessionId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key(sessionId)));
    }

    public List<ChatMessage> findMessages(String sessionId) {
        String raw = stringRedisTemplate.opsForValue().get(key(sessionId));
        if (raw == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(raw, MESSAGE_LIST_TYPE);
        } catch (JsonProcessingException e) {
            log.warn("[CHAT] 대화 기록 역직렬화 실패 sessionId={}", sessionId, e);
            return List.of();
        }
    }

    public void append(String sessionId, ChatMessage message) {
        List<ChatMessage> messages = new ArrayList<>(findMessages(sessionId));
        messages.add(message);
        if (messages.size() > MAX_MESSAGES) {
            messages = messages.subList(messages.size() - MAX_MESSAGES, messages.size());
        }
        write(sessionId, messages);
    }

    private void write(String sessionId, List<ChatMessage> messages) {
        try {
            stringRedisTemplate.opsForValue()
                    .set(key(sessionId), objectMapper.writeValueAsString(messages), TTL);
        } catch (JsonProcessingException e) {
            log.warn("[CHAT] 대화 기록 직렬화 실패 sessionId={}", sessionId, e);
        }
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}
