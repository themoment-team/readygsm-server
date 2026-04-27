package team.themoment.readygsmserver.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.themoment.readygsmserver.global.security.oauth2.feign.GoogleTokenClient;
import team.themoment.readygsmserver.global.security.oauth2.feign.GoogleUserInfoClient;

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
}
