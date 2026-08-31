package team.themoment.readygsmserver.global.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
