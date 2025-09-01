package com.codeit.discodeit8.controller;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit8.entity.BinaryContent;
import com.codeit.discodeit8.mapper.BinaryContentMapper;
import com.codeit.discodeit8.service.BinaryContentService;
import com.codeit.discodeit8.swagger.SwaggerBinaryContentController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "BinaryContent", description = "첨부파일 API")
@RequestMapping("/api/binaryContents")
public class BinaryContentsController implements SwaggerBinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;

  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllBinaryContents(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContentResponseList =
        binaryContentService.findBinaryContentsByBinaryContentIds(binaryContentIds);
    return ResponseEntity.ok(binaryContentResponseList);
  }

  @GetMapping(value = "/{binaryContentId}")
  public ResponseEntity<BinaryContent> findBinaryContentById(
      @PathVariable("binaryContentId") UUID binaryContentId) {

    BinaryContent binaryContent = binaryContentService.findBinaryContentByBinaryContentId(
        binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    log.info("[GET /{binaryContentId}/download] 다운로드 요청 수신: binaryContentId={}", binaryContentId);

    BinaryContent binaryContent = binaryContentService.findBinaryContentByBinaryContentId(
        binaryContentId);
    BinaryContentDto binaryContentDto = binaryContentMapper.toBinaryContentDto(binaryContent);

    ResponseEntity<?> response = binaryContentService.download(binaryContentDto);

    log.info("[GET /{binaryContentId}/download] 다운로드 처리 완료: binaryContentId={}", binaryContentId);
    return response;
  }

}