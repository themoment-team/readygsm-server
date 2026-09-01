package team.themoment.readygsmserver.domain.chat.entity;

import team.themoment.readygsmserver.domain.chat.entity.constant.ChatRole;

/**
 * 대화 한 턴. 요청으로 들어온 role은 신뢰하지 않으므로 서버가 직접 만드는 값만 존재한다.
 */
public record ChatMessage(
        ChatRole role,
        String content
) {

    public static ChatMessage user(String content) {
        return new ChatMessage(ChatRole.USER, content);
    }

    public static ChatMessage assistant(String content) {
        return new ChatMessage(ChatRole.ASSISTANT, content);
    }
}
