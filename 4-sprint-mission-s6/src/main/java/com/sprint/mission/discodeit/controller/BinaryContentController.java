package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.BinaryContentDownloadResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(path = "{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    BinaryContentDto dto = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok().body(dto);
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContentDto> allByIdIn = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok().body(allByIdIn);
  }

  @GetMapping(path = "{binaryContentId}/download")
  public ResponseEntity<Resource> downloadBinaryContent (@PathVariable UUID binaryContentId)
  {
    BinaryContentDto dto = binaryContentService.find(binaryContentId);

    return binaryContentStorage.download(dto);
  }
}
