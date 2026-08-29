package team.themoment.readygsmserver.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.themoment.readygsmserver.domain.chat.dto.response.ChatSessionResDto;
import team.themoment.readygsmserver.domain.chat.service.CreateChatSessionService;

@RestController
@Tag(name = "Chat", description = "FAQ 챗봇 API")
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final CreateChatSessionService createChatSessionService;

    @Operation(
            summary = "채팅 세션 발급",
            description = "채팅창을 열 때 호출합니다. 발급된 sessionId를 이후 질문 요청의 X-Session-Id 헤더에 실어 보냅니다. 30분간 유효합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 성공")
    })
    @PostMapping("/session")
    public ChatSessionResDto createSession() {
        return createChatSessionService.execute();
    }
}
