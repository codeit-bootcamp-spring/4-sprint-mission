package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(
      @PathVariable("binaryContentId") UUID binaryContentId) {

    log.info("HTTP Find BinaryContent requested: id={}", binaryContentId);

    BinaryContentDto binaryContent = binaryContentService.find(binaryContentId);

    log.info("HTTP Find BinaryContent succeeded: id={}", binaryContentId);

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContent);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {

    log.info("HTTP FindAll BinaryContent requested: count={}", binaryContentIds.size());

    List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);

    log.info("HTTP FindAll BinaryContent succeeded: requestedCount={}, returnedCount={}",
            binaryContentIds.size(), binaryContents.size());

    return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContents);
  }

  @PostMapping("/upload")
  public ResponseEntity<BinaryContentDto> upload(@RequestParam("file") MultipartFile file) {
    try {
      log.info("HTTP Upload BinaryContent requested: fileName={}", file.getOriginalFilename());

      BinaryContentCreateRequest request = new BinaryContentCreateRequest(
              file.getOriginalFilename(),
              file.getContentType(),
              file.getBytes()
      );

      BinaryContentDto result = binaryContentService.create(request);

      log.info("HTTP Upload BinaryContent succeeded: id={}", result.id());

      return ResponseEntity
              .created(URI.create("/api/binaryContents/" + result.id()))
              .body(result);

    } catch (Exception e) {
      log.error("HTTP Upload BinaryContent failed: fileName={}", file.getOriginalFilename(), e);
      throw new RuntimeException("파일 업로드 실패", e);
    }
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<?> download(
      @PathVariable("binaryContentId") UUID binaryContentId) {

    log.debug("HTTP Download BinaryContent requested: id={}", binaryContentId);

    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

    log.info("HTTP Download BinaryContent succeeded: id={}", binaryContentId);

    return binaryContentStorage.download(binaryContentDto);
  }
}
