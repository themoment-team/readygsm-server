package team.themoment.readygsmserver.global.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "oauth2")
public record OAuth2Properties(
        String successRedirectUrl,
        String failureRedirectUrl,
        List<String> allowedRedirectUris
) {
}
