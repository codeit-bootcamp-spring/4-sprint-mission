package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.exception.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();

        ModelConverters.getInstance().read(ErrorResponse.class).forEach(components::addSchemas);
        ModelConverters.getInstance().read(ErrorResponse.FieldError.class).forEach(components::addSchemas);
        ModelConverters.getInstance().read(ErrorResponse.ConstraintViolationError.class).forEach(components::addSchemas);
        return new OpenAPI()
                .info(new Info()
                        .title("Discodeit API 문서")
                        .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
                        .version("1.0.0")
                        .contact(new Contact().name("김민수").email("kms_1015@naver.com")))
                .components(components);
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(path -> {
            path.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                responses.addApiResponse("400", createApiResponse("Bad Request", createRefSchema("ErrorResponse")));
                responses.addApiResponse("404", createApiResponse("Not Found", createRefSchema("ErrorResponse")));
                responses.addApiResponse("500", createApiResponse("Server Error", createRefSchema("ErrorResponse")));
            });
        });
    }

    private ApiResponse createApiResponse(String messsage, Schema schema) {
        MediaType mediaType = new MediaType();
        mediaType.setSchema(schema);
        return new ApiResponse().description(messsage).content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType));
    }

    private Schema<?> createRefSchema(String schemaName) {
        return new Schema<>().$ref("#/components/schemas/" + schemaName);
    }
}
