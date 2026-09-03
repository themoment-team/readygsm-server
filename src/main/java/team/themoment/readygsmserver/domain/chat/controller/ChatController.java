package team.themoment.readygsmserver.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import team.themoment.readygsmserver.domain.chat.service.ChatRateLimiter;
import team.themoment.readygsmserver.domain.chat.service.CreateChatSessionService;
import team.themoment.readygsmserver.domain.chat.service.StreamChatAnswerService;
import team.themoment.readygsmserver.global.security.annotation.AuthRequest;

@RestController
@Tag(name = "Chat", description = "FAQ 챗봇 API")
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final CreateChatSessionService createChatSessionService;
    private final StreamChatAnswerService streamChatAnswerService;
    private final ChatRateLimiter chatRateLimiter;

    @Operation(
            summary = "채팅 세션 발급",
            description = """
                    채팅창을 열 때 호출합니다. 로그인이 필요합니다.

                    발급된 sessionId를 이후 질문 요청의 X-Session-Id 헤더에 실어 보냅니다. 30분간 유효하며 질문할 때마다 갱신됩니다.

                    대화 이력을 돌려주는 API는 없습니다. 새로고침 등으로 화면이 비면 이전 sessionId를 버리고 새로 발급받으세요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발급 성공"),
            @ApiResponse(responseCode = "403", description = "로그인이 필요함"),
            @ApiResponse(responseCode = "429", description = "요청이 너무 많음")
    })
    @PostMapping("/session")
    public ChatSessionResDto createSession(@AuthRequest Long userId) {
        chatRateLimiter.validateSessionCreation(userId);
        return createChatSessionService.execute(userId);
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
            @ApiResponse(
                    responseCode = "200",
                    description = "스트림 시작",
                    content = @Content(
                            mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(value = """
                                    data: 원서 접

                                    data: 수 시에

                                    event: done
                                    data: {"finishReason":"stop"}
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "질문이 비었거나 500자를 초과함"),
            @ApiResponse(responseCode = "403", description = "로그인이 필요함"),
            @ApiResponse(responseCode = "404", description = "만료되었거나, 존재하지 않거나, 본인의 것이 아닌 세션"),
            @ApiResponse(responseCode = "429", description = "요청이 너무 많음")
    })
    @PostMapping
    public ResponseEntity<SseEmitter> ask(
            @AuthRequest Long userId,
            @Parameter(description = "세션 발급 API로 받은 sessionId", required = true)
            @RequestHeader("X-Session-Id") String sessionId,
            @Valid @RequestBody ChatAskReqDto req
    ) {
        chatRateLimiter.validateAsk(userId);
        return ResponseEntity.ok()
                // no-transform이 없으면 프론트의 Next.js dev 서버가 text/event-stream을 gzip으로 압축한다.
                // 이벤트마다 flush하지 않아 응답 전체가 한 덩어리로 도착한다
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-transform")
                .header("X-Accel-Buffering", "no")
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(streamChatAnswerService.execute(userId, sessionId, req));
    }
}
