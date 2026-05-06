package team.themoment.readygsmserver.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("google.oauth")
public record GoogleOAuthProperties(String clientId, String clientSecret) {
}
