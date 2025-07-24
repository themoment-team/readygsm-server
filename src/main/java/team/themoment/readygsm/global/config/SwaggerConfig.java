package team.themoment.readygsm.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import lombok.SneakyThrows;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.themoment.readygsm.global.response.CommonApiResponse;

@OpenAPIDefinition(
        info = @Info(title = "Ready, GSM",
                description = "광주소프트웨어마이스터고 교외참여활동 관리 서비스",
                version = "v1"))
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api(OperationCustomizer operationCustomizer) {
        return GroupedOpenApi.builder()
                .group("Ready, GSM API")
                .pathsToMatch("/users/**", "/auth/**", "/reservation/**", "/activity/**")
                .addOperationCustomizer(operationCustomizer)
                .build();
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            Class<?> returnType = handlerMethod.getMethod().getReturnType();
            this.addResponseBodyWrapperSchemaExample(operation, CommonApiResponse.class.isAssignableFrom(returnType));

            return operation;
        };
    }

    private void addResponseBodyWrapperSchemaExample(Operation operation, boolean hideDataField) {
        final Content content = operation.getResponses().get("200").getContent();
        if (content != null) {
            content.keySet()
                    .forEach(mediaTypeKey -> {
                        final MediaType mediaType = content.get(mediaTypeKey);
                        Schema<?> originalSchema = mediaType.getSchema();
                        mediaType.schema(wrapSchema(originalSchema, hideDataField));
                    });
        }
    }

    @SneakyThrows
    private Schema<?> wrapSchema(Schema<?> originalSchema, boolean hideDataField) {
        final Schema<?> wrapperSchema = new Schema<>();

        wrapperSchema.addProperty("status", new Schema<>().type("string").example("OK"));
        wrapperSchema.addProperty("code", new Schema<>().type("integer").example(200));
        wrapperSchema.addProperty("message", new Schema<>().type("string").example("OK"));
        if (!hideDataField) wrapperSchema.addProperty("data", originalSchema);

        return wrapperSchema;
    }
}