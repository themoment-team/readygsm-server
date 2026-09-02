package team.themoment.readygsmserver.domain.chat.entity;

import java.util.List;

/**
 * 세션 하나에 대응하는 대화.
 *
 * <p>소유자를 함께 저장한다. sessionId는 추측하기 어렵지만 그것만으로 접근을 막으면
 * 유출된 sessionId 하나로 남의 대화를 이어받을 수 있다.
 */
public record Conversation(
        Long userId,
        List<ChatMessage> messages
) {

    public static Conversation empty(Long userId) {
        return new Conversation(userId, List.of());
    }

    public boolean ownedBy(Long candidate) {
        return userId != null && userId.equals(candidate);
    }
}
