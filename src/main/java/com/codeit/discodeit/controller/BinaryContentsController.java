package com.codeit.discodeit.controller;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.mapper.BinaryContentMapper;
import com.codeit.discodeit.service.BinaryContentService;
import com.codeit.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부파일 API")
@RequestMapping("/api/binaryContents")// 요수항에 URL 지시사항 잉 있어 카멜케이스 안씀, 요구사항에
public class BinaryContentsController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;

  @Operation(summary = "여러 첨부 파일 조회")
  @ApiResponse(
      responseCode = "200",
      description = "첨부 파일 목록 조회 성공",
      content = @Content(
          mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = BinaryContent.class))
      )
  )
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllBinaryContents(
      @Parameter(
          name = "binaryContentIds",
          description = "조회할 첨부 파일 ID 목록",
          required = true
      )
      @RequestParam List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContentResponseList =
        binaryContentService.findBinaryContentsByBinaryContentIds(binaryContentIds);
    return ResponseEntity.ok(binaryContentResponseList);
  }

  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  @Operation(
      summary = "첨부 파일 조회",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "첨부 파일 조회 성공",
              content = @Content(
                  mediaType = "*/*",
                  schema = @Schema(implementation = BinaryContent.class)
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "첨부 파일을 찾을 수 없음",
              content = @Content(mediaType = "*/*", examples = {
                  @io.swagger.v3.oas.annotations.media.ExampleObject(
                      value = "BinaryContent with id {binaryContentId} not found"
                  )
              })
          )
      }
  )
  public ResponseEntity<BinaryContent> findBinaryContentById(
      @Parameter(description = "조회할 첨부 파일 ID", required = true)
      @PathVariable("binaryContentId") UUID binaryContentId) {

    BinaryContent binaryContent = binaryContentService.findBinaryContentByBinaryContentId(
        binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    // 실제 파일 데이터 및 메타데이터 가져오기

    BinaryContent binaryContent = binaryContentService.findBinaryContentByBinaryContentId(binaryContentId);
    BinaryContentDto binaryContentDto = binaryContentMapper.toBinaryContentDto(binaryContent);

    return binaryContentService.download(binaryContentDto);
  }
}