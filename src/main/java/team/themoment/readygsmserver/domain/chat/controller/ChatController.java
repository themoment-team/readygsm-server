package team.themoment.readygsmserver.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import team.themoment.readygsmserver.domain.chat.dto.request.ChatAskReqDto;
import team.themoment.readygsmserver.domain.chat.dto.response.ChatSessionResDto;
import team.themoment.readygsmserver.domain.chat.service.CreateChatSessionService;
import team.themoment.readygsmserver.domain.chat.service.StreamChatAnswerService;

@RestController
@Tag(name = "Chat", description = "FAQ 챗봇 API")
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final CreateChatSessionService createChatSessionService;
    private final StreamChatAnswerService streamChatAnswerService;

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

    /**
     * 응답이 SSE라 {@code produces}를 지정하지 않는다.
     * 지정하면 세션 검증 실패 시 내려가는 JSON 오류 응답이 협상에서 걸린다.
     */
    @Operation(
            summary = "질문하기",
            description = """
                    질문에 대한 답변을 SSE로 스트리밍합니다.

                    - `data:` 답변 토큰 조각. 누적해서 렌더링합니다
                    - `event: done` 정상 완료. 이 이벤트를 받았을 때만 정상 완료로 간주합니다
                    - `event: error` 서버가 인지한 실패. 재시도할 수 있습니다
                    - 아무것도 없이 끊기면 비정상입니다
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스트림 시작"),
            @ApiResponse(responseCode = "400", description = "질문이 비었거나 500자를 초과함"),
            @ApiResponse(responseCode = "404", description = "만료되었거나 존재하지 않는 세션")
    })
    @PostMapping
    public ResponseEntity<SseEmitter> ask(
            @Parameter(description = "세션 발급 API로 받은 sessionId", required = true)
            @RequestHeader("X-Session-Id") String sessionId,
            @Valid @RequestBody ChatAskReqDto req
    ) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(streamChatAnswerService.execute(sessionId, req));
    }
}
