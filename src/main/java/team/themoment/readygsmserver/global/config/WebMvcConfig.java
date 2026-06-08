package team.themoment.readygsmserver.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import team.themoment.readygsmserver.global.security.oauth2.OAuth2Properties;
import team.themoment.readygsmserver.global.security.resolver.AuthenticationArgumentResolver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final OAuth2Properties oauth2Properties;
    private final AuthenticationArgumentResolver authenticationArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = oauth2Properties.allowedRedirectUris().stream()
                .map(uri -> {
                    try {
                        URI parsed = new URI(uri);
                        int port = parsed.getPort();
                        return parsed.getScheme() + "://" + parsed.getHost() +
                               (port != -1 ? ":" + port : "");
                    } catch (URISyntaxException e) {
                        throw new IllegalStateException("Invalid redirect URI in config: " + uri, e);
                    }
                })
                .distinct()
                .toArray(String[]::new);

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationArgumentResolver);
    }
}
