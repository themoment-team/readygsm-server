package team.themoment.readygsmserver.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kakao.oauth")
public record KakaoOAuthProperties(String clientId, String clientSecret, String redirectUri) {
}