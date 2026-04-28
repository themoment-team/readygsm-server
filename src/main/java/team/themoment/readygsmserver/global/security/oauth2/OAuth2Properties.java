package team.themoment.readygsmserver.global.security.oauth2;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "oauth2")
public record OAuth2Properties(
        String successRedirectUrl,
        String failureRedirectUrl,
        @NotNull List<String> allowedRedirectUris
) {
}
