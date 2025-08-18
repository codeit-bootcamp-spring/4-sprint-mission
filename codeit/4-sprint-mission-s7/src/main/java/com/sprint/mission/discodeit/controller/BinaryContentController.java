package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<?> download(
      @PathVariable("binaryContentId") UUID binaryContentId) {

    log.debug("HTTP Download BinaryContent requested: id={}", binaryContentId);

    BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);

    log.info("HTTP Download BinaryContent succeeded: id={}", binaryContentId);

    return binaryContentStorage.download(binaryContentDto);
  }
}
