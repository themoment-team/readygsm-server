package team.themoment.readygsmserver.domain.chat.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import team.themoment.readygsmserver.domain.chat.entity.ChatMessage;
import team.themoment.readygsmserver.domain.chat.entity.Conversation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 대화 기록을 Redis에 보관한다.
 *
 * <p>대화가 없는 새 세션도 실제로 저장한다. 키의 존재 여부만으로 만료·위조된 세션과
 * 아직 대화가 없는 새 세션을 구분하기 위해서다.
 *
 * <p>메시지와 소유자를 한 키에 함께 담는다. 나눠 담으면 TTL이 따로 돌아
 * 소유자만 먼저 사라진 대화가 생길 수 있다.
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

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void create(String sessionId, Long userId) {
        write(sessionId, Conversation.empty(userId));
    }

    /**
     * @return 없거나 읽을 수 없으면 {@link Optional#empty()}. 읽기 실패는 만료와 똑같이 다룬다.
     */
    public Optional<Conversation> find(String sessionId) {
        String raw = stringRedisTemplate.opsForValue().get(key(sessionId));
        if (raw == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(raw, Conversation.class));
        } catch (JsonProcessingException e) {
            log.warn("[CHAT] 대화 기록 역직렬화 실패 sessionId={}", sessionId, e);
            return Optional.empty();
        }
    }

    /**
     * 소유자는 저장된 값을 그대로 유지한다.
     *
     * <p>스트리밍 도중 세션이 만료됐다면 저장하지 않는다. 여기서 새로 쓰면
     * 소유자 없는 대화가 되살아난다.
     */
    public void append(String sessionId, ChatMessage message) {
        Optional<Conversation> found = find(sessionId);
        if (found.isEmpty()) {
            log.debug("[CHAT] 만료된 세션이라 메시지를 저장하지 않습니다 sessionId={}", sessionId);
            return;
        }
        Conversation conversation = found.get();

        List<ChatMessage> messages = new ArrayList<>(conversation.messages());
        messages.add(message);
        if (messages.size() > MAX_MESSAGES) {
            messages = messages.subList(messages.size() - MAX_MESSAGES, messages.size());
        }
        write(sessionId, new Conversation(conversation.userId(), messages));
    }

    private void write(String sessionId, Conversation conversation) {
        try {
            stringRedisTemplate.opsForValue()
                    .set(key(sessionId), objectMapper.writeValueAsString(conversation), TTL);
        } catch (JsonProcessingException e) {
            log.warn("[CHAT] 대화 기록 직렬화 실패 sessionId={}", sessionId, e);
        }
    }

    private String key(String sessionId) {
        return KEY_PREFIX + sessionId;
    }
}
