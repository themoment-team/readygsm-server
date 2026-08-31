package team.themoment.readygsmserver.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * OpenAI 호출 설정.
 *
 * @param apiKey mock 모드에서도 부팅해야 해서 여기서는 검증하지 않는다.
 *               openai 모드일 때의 검증은 OpenAiChatCompletionClient 생성자에 있다.
 */
@Validated
@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
        String apiKey,
        @NotBlank String baseUrl,
        @NotBlank String model,
        @Positive int maxTokens,
        double temperature
) {
}
