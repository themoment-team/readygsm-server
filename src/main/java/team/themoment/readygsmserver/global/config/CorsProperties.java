package team.themoment.readygsmserver.global.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        @NotNull List<String> allowedOrigins
) {
}
