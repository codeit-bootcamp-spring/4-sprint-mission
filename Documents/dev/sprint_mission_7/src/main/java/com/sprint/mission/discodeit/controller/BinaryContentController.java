package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부 파일 API")
@RestController
@RequestMapping("/api/binaryContents")
@Slf4j
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    public BinaryContentController(BinaryContentService binaryContentService, BinaryContentStorage binaryContentStorage) {
        this.binaryContentService = binaryContentService;
        this.binaryContentStorage = binaryContentStorage;
    }

    @Operation(summary = "첨부 파일 조회", description = "단일 BinaryContent ID를 기반으로 파일을 조회합니다.", operationId = "find")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BinaryContentDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                     "messageId": "uuid...",
                                                     "content": "내일 참석 멤버가이 어떻게 되나요",
                                                     "channelId": "uuid...",
                                                     "authorId": "uuid...",
                                                     "attachmentId": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "첨부 파일을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "BINARY_CONTENT_NOT_FOUND",
                                              "message": "BinaryContent with id {binaryContentId} not found"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> getBinaryContent(@PathVariable UUID binaryContentId) {
        BinaryContentDto dto = binaryContentService.find(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(summary = "여러 첨부 파일 조회", description = "지정된 BinaryContent ID 목록을 기반으로 여러 파일을 조회합니다.", operationId = "findAllByIdIn")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "첨부 파일 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BinaryContentDto.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                     "messageId": "uuid...",
                                                     "content": "내일 참석 멤버가 어떻게 되나요",
                                                     "channelId": "uuid...",
                                                     "authorId": "uuid...",
                                                     "attachmentId": null
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> getBinaryContents(@RequestParam List<UUID> fileIds) {
        List<BinaryContentDto> files = binaryContentService.findAllByIdIn(fileIds);
        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?>download(@PathVariable UUID binaryContentId) {
        log.info("첨부 파일 다운로드 요청 수신: binaryContentId={}", binaryContentId);
        try {
            BinaryContentDto dto = binaryContentService.find(binaryContentId);
            log.info("첨부 파일 다운로드 성공: binaryContentId={}", binaryContentId);
            return binaryContentStorage.download(dto);
        } catch (IOException e) {
            log.error("첨부 파일 다운로드 실패: binaryContentId={}", binaryContentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 다운로드에 실패했습니다.");
        }

    }
}


