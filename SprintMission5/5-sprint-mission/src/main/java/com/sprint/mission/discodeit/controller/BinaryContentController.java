package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;

  @Operation(summary = "새로운 BinaryContent 생성", description = "파일을 첨부하여 BinaryContent를 생성합니다.")
  @PostMapping
  public ResponseEntity<BinaryContent> createBinaryContent(
      @ModelAttribute BinaryContentCreateServiceRequest dto) {
    return ResponseEntity.ok(binaryContentService.createBinaryContent(dto));
  }

  @GetMapping("/multiple")
  public ResponseEntity<List<BinaryContent>> getBinaryContents(@RequestParam UUID id) {
    if (id == null) {
      return ResponseEntity.badRequest().body(null);
    }
    List<BinaryContent> binaryContents =
        binaryContentService.findAllBinaryContentById(id);
    return ResponseEntity.ok(binaryContents);
  }

  @GetMapping("/single")
  public ResponseEntity<BinaryContent> getBinaryContent(
      @RequestParam("binary-content-id") UUID binaryContentId) {
    if (binaryContentId == null) {
      return ResponseEntity.badRequest().body(null);
    }
    BinaryContent binaryContent =
        binaryContentService.findBinaryContentById(binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }

  @Operation(summary = "BinaryContent 엔티티 정보 조회", description = "BinaryContent ID로 엔티티 정보를 조회합니다.")
  @GetMapping("/find")
  public ResponseEntity<BinaryContentEntity> getBinaryContentEntity(
      @RequestParam("binary-content-id") UUID binaryContentId) {
    if (binaryContentId == null) {
      return ResponseEntity.badRequest().body(null);
    }
    BinaryContent binaryContent =
        binaryContentService.findBinaryContentById(binaryContentId);

    return ResponseEntity.ok(
        binaryContentMapper.BinaryContentResponseDtoToBinaryContent(binaryContent));
  }

  @Operation(summary = "BinaryContent ID로 단일 조회", description = "PathVariable 방식으로 파일 정보를 조회합니다.")
  @GetMapping("/{id}")
  public ResponseEntity<BinaryContent> getBinaryContentByPath(@PathVariable UUID id) {
    BinaryContent binaryContent = binaryContentService.findBinaryContentById(id);
    return ResponseEntity.ok(binaryContent);
  }
}
