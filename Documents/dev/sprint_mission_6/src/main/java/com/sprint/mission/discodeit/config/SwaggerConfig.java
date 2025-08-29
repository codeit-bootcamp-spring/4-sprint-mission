package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Discodeit API 문서,",
                description = "Discodeit 프로젝트의 Swagger API 문서입니다.",
                version = "v1"
        ),
        servers = @Server(url = "http://localhost:8080", description = "로컬 서버")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomizer removeDefaultBadRequest() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths != null) {
                for(PathItem pathItem : paths.values()) {
                    for(Operation operation : pathItem.readOperations()) {
                        ApiResponses responses = operation.getResponses();
                        if(responses != null) {
                            responses.remove("400");
                        }
                    }
                }
            }
        };
    }
}
