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

@Configuration
public class FeignClientConfig {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Bean
    public GoogleTokenClient googleTokenClient() {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .target(GoogleTokenClient.class, "https://oauth2.googleapis.com");
    }

    @Bean
    public GoogleUserInfoClient googleUserInfoClient() {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .target(GoogleUserInfoClient.class, "https://www.googleapis.com");
    }

    @Bean
    public KakaoTokenClient kakaoTokenClient() {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .target(KakaoTokenClient.class, "https://kauth.kakao.com");
    }

    @Bean
    public KakaoUserInfoClient kakaoUserInfoClient() {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .target(KakaoUserInfoClient.class, "https://kapi.kakao.com");
    }
}
