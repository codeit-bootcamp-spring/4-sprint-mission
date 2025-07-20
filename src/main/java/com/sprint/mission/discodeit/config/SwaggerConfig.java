package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Discodeit")
                        .version("v1.0")
                        .description("Discodeit RESTful API 문서")
                        .contact(new Contact()
                                .name("김민정")
                                .email("minbory925@gmail.com")
                                .url("https://minsllogg.tistory.com/")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/Mingguriguri/4-sprint-mission/tree/minjeong-sprint4"));
    }

    /**
     * Internal Server Error 500과 같이 공통적인 에러는 일괄적으로 코드를 설정하고자 아래 클래스를 추가하게 되었습니다.
     * 아래 부분은 제 힘이 아니라 강사님과 chatGPT의 힘을 통해 구현한 코드입니다.
    */
    @Bean
    public OperationCustomizer globalResponseCustomizer() {
        return (operation, handlerMethod) -> {
            Schema<?> errorSchema = new ObjectSchema()
                    .addProperties("success", new StringSchema().example("false"))
                    .addProperties("code", new IntegerSchema().example(500))
                    .addProperties("message", new StringSchema().example("알 수 없는 오류가 발생했습니다."))
                    .addProperties("data", new ObjectSchema().nullable(true).example(null))
                    .addProperties("timestamp", new DateTimeSchema().example("2025-07-09T17:25:06.099774"));

            ApiResponse response500 = new ApiResponse()
                    .description("서버 내부 오류")
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType().schema(errorSchema)
                            )
                    );

            operation.getResponses().addApiResponse("500", response500);
            return operation;
        };
    }
}
