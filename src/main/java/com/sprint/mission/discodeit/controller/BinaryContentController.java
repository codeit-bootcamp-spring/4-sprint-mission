package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binaryContent")
@Tag(name = "첨부파일 (BinaryContent)", description = "첨부 파일 조회와 관련된 API입니다.")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @Operation(summary = "특정 첨부 파일을 ID를 통해 조회합니다.")
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> find(
      @Parameter(description = "조회할 첨부 파일의 고유 ID")
      @PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContent);
  }

  @Operation(summary = "여러 첨부 파일을 ID를 통해 조회합니다.")
  @GetMapping()
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
      @Parameter(description = "조회할 첨부 파일 ID 목록")
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }
}
