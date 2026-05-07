package team.themoment.readygsmserver.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.themoment.readygsmserver.global.security.oauth2.feign.GoogleTokenClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.GoogleUserInfoClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.KakaoTokenClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.KakaoUserInfoClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.OAuthErrorDecoder;

@Configuration
public class FeignClientConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final OAuthErrorDecoder oAuthErrorDecoder = new OAuthErrorDecoder();

    private Feign.Builder baseBuilder() {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .errorDecoder(oAuthErrorDecoder);
    }

    @Bean
    public GoogleTokenClient googleTokenClient() {
        return baseBuilder().target(GoogleTokenClient.class, "https://oauth2.googleapis.com");
    }

    @Bean
    public GoogleUserInfoClient googleUserInfoClient() {
        return baseBuilder().target(GoogleUserInfoClient.class, "https://www.googleapis.com");
    }

    @Bean
    public KakaoTokenClient kakaoTokenClient() {
        return baseBuilder().target(KakaoTokenClient.class, "https://kauth.kakao.com");
    }

    @Bean
    public KakaoUserInfoClient kakaoUserInfoClient() {
        return baseBuilder().target(KakaoUserInfoClient.class, "https://kapi.kakao.com");
    }
}
