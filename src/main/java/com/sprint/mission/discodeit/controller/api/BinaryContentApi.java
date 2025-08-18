package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Validated
public interface BinaryContentApi {
    @Operation(summary = "여러 첨부 파일 조회", operationId = "findAllByIdIn", responses = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BinaryContentDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity findBinaryContents(@Parameter(description = "조회할 첨부 파일 ID 목록")
                                      @RequestParam("binaryContentIds") List<@NotNull(message = "잘못된 ID 입니다.") UUID> binaryContentIds);

    @Operation(summary = "첨부 파일 조회", operationId = "find", responses = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공", content = @Content(schema = @Schema(implementation = BinaryContentDto.class))),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "BinaryContent with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity findBinaryContent(@Parameter(description = "조회할 첨부 파일 ID")
                                     @NotNull(message = "잘못된 ID 입니다.")
                                     @PathVariable("binary-content-id") UUID binaryContentId);

    @Operation(summary = "파일 다운로드", operationId = "download", responses = {
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공", content = @Content(schema = @Schema(format = "binary", type = "string"))),
    })
    ResponseEntity downloadBinaryContent(@Parameter(schema = @Schema(format = "uuid"))
                                         @NotNull(message = "잘못된 ID 입니다.")
                                         @PathVariable("binaryContentId") UUID binaryContentId) throws IOException;
}
