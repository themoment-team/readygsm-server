package team.themoment.readygsmserver.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.themoment.readygsmserver.domain.chat.dto.response.ChatSessionResDto;
import team.themoment.readygsmserver.domain.chat.repository.ConversationRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateChatSessionService {

    private final ConversationRepository conversationRepository;

    public ChatSessionResDto execute() {
        String sessionId = UUID.randomUUID().toString();
        conversationRepository.create(sessionId);
        log.debug("[CHAT] 채팅 세션 생성 sessionId={}", sessionId);
        return new ChatSessionResDto(sessionId);
    }
}
