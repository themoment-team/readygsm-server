package team.themoment.readygsmserver.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_AUTH = "Bearer Authentication";

    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> openApi
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(
                        (openApi.getComponents() != null ? openApi.getComponents() : new Components())
                                .addSecuritySchemes(BEARER_AUTH,
                                        new SecurityScheme()
                                                .name(BEARER_AUTH)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)
                                )
                );
    }
}