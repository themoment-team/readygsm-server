package team.themoment.readygsmserver.domain.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import team.themoment.readygsmserver.domain.chat.entity.ChatMessage;
import team.themoment.readygsmserver.global.config.OpenAiProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * OpenAI Chat Completions API를 스트리밍으로 호출한다.
 *
 * <p>SDK 대신 {@link WebClient}를 직접 쓴다. 청크를 그대로 다뤄야 해서 제어가 쉽다.
 *
 * <p>OpenAI의 응답 스키마는 여기서 끝난다. 바깥으로는 토큰 문자열과 finishReason만 나간다.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "chat", name = "client", havingValue = "openai")
public class OpenAiChatCompletionClient implements ChatCompletionClient {

    private static final String CHAT_COMPLETIONS_PATH = "/v1/chat/completions";
    private static final String DONE_MARKER = "[DONE]";
    private static final String ROLE_SYSTEM = "system";
    private static final String DEFAULT_FINISH_REASON = "stop";

    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    /**
     * 콜백을 SSE 전송 전용 풀로 넘기기 위한 스케줄러.
     * 이벤트 루프 스레드에서 블로킹 쓰기를 하면 리액터가 통째로 막힌다.
     */
    private final Scheduler sendScheduler;

    public OpenAiChatCompletionClient(
            OpenAiProperties openAiProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder,
            @Qualifier("chatStreamExecutor") Executor chatStreamExecutor
    ) {
        this.openAiProperties = openAiProperties;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .baseUrl(openAiProperties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiProperties.apiKey())
                .build();
        this.sendScheduler = Schedulers.fromExecutor(chatStreamExecutor);
    }

    @Override
    public StreamSubscription stream(String systemPrompt, List<ChatMessage> messages, StreamHandler handler) {
        AtomicReference<String> finishReason = new AtomicReference<>(DEFAULT_FINISH_REASON);

        Disposable disposable = webClient.post()
                .uri(CHAT_COMPLETIONS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(buildRequestBody(systemPrompt, messages))
                .retrieve()
                .bodyToFlux(String.class)
                .publishOn(sendScheduler)
                .subscribe(
                        chunk -> handleChunk(chunk, finishReason, handler),
                        handler::onError,
                        () -> handler.onComplete(finishReason.get())
                );

        return disposable::dispose;
    }

    private Map<String, Object> buildRequestBody(String systemPrompt, List<ChatMessage> messages) {
        List<Map<String, String>> payload = new ArrayList<>();
        payload.add(Map.of("role", ROLE_SYSTEM, "content", systemPrompt));
        for (ChatMessage message : messages) {
            payload.add(Map.of(
                    "role", message.role().name().toLowerCase(),
                    "content", message.content()
            ));
        }
        return Map.of(
                "model", openAiProperties.model(),
                "messages", payload,
                "max_tokens", openAiProperties.maxTokens(),
                "temperature", openAiProperties.temperature(),
                "stream", true
        );
    }

    /**
     * {@code bodyToFlux(String.class)}는 SSE의 {@code data:} 접두어를 벗겨낸 본문만 넘겨준다.
     * 따라서 여기 들어오는 값은 JSON 한 덩어리이거나 종료 표식이다.
     */
    private void handleChunk(String chunk, AtomicReference<String> finishReason, StreamHandler handler) {
        if (DONE_MARKER.equals(chunk.trim())) {
            return;
        }
        try {
            JsonNode choice = objectMapper.readTree(chunk).path("choices").path(0);

            JsonNode reason = choice.path("finish_reason");
            if (!reason.isMissingNode() && !reason.isNull()) {
                finishReason.set(reason.asText());
            }

            JsonNode content = choice.path("delta").path("content");
            if (content.isTextual() && !content.asText().isEmpty()) {
                handler.onToken(content.asText());
            }
        } catch (Exception e) {
            log.warn("[CHAT] OpenAI 청크 파싱 실패, 건너뜁니다 chunk={}", chunk, e);
        }
    }
}
