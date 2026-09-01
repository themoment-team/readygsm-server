package team.themoment.readygsmserver.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4는 기본적으로 Jackson 3(tools.jackson) ObjectMapper만 자동 구성하므로,
 * 클래식 Jackson 2(com.fasterxml.jackson.databind) ObjectMapper를 쓰는 코드를 위해 직접 빈으로 등록한다.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
