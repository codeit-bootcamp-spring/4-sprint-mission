package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto.*;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @Operation(summary = "첨부 파일 조회", description = "첨부 파일 ID로 파일 정보를 조회합니다.",
            operationId = "find")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = BinaryContentResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject(value = "{\"message\": \"BinaryContent with id {binaryContentId} not found\"}")
                    )
            )
    })
    @GetMapping(value = "/{binaryContentId}")
    public ResponseEntity<BinaryContentResponse> find(
                                            @Parameter(description = "조회할 첨부 파일 ID")
                                            @PathVariable("binaryContentId") UUID id) {
        BinaryContentResponse result = binaryContentService.findById(id);
        return ResponseEntity.ok(result);
    }



    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공",
                    content = @Content(mediaType = "*/*",
                            array = @ArraySchema(schema = @Schema(implementation = BinaryContentResponse.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
                                                                    @Parameter(description = "조회할 첨부 파일 ID 목록")
                                                                    @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContentResponse> result = binaryContentService.findAllByIdIn(binaryContentIds);
        return ResponseEntity.ok(result);
    }
}
