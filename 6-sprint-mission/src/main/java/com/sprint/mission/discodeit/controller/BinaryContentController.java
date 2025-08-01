package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateServiceRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Base64;
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
    private final BinaryContentStorage binaryContentStorage;

    @Operation(summary = "새로운 BinaryContent 생성", description = "파일을 첨부하여 BinaryContent를 생성합니다.")
    @PostMapping
    public ResponseEntity<BinaryContentDto> createBinaryContent(
            @ModelAttribute BinaryContentCreateServiceRequest dto) {
        return ResponseEntity.ok(binaryContentService.createBinaryContent(dto));
    }

    @GetMapping("/multiple")
    public ResponseEntity<List<BinaryContentDto>> getBinaryContents(@RequestParam UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<BinaryContentDto> binaryContentDtos = binaryContentService.findAllBinaryContentById(id);
        return ResponseEntity.ok(binaryContentDtos);
    }

    @GetMapping("/single")
    public ResponseEntity<BinaryContentDto> getBinaryContent(
            @RequestParam("binary-content-id") UUID binaryContentId) {
        if (binaryContentId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        BinaryContentDto binaryContentDto = binaryContentService.findBinaryContentById(binaryContentId);
        return ResponseEntity.ok(binaryContentDto);
    }

    @Operation(summary = "BinaryContent 엔티티 정보 조회", description = "BinaryContent ID로 엔티티 정보를 조회합니다.")
    @GetMapping("/find")
    public ResponseEntity<BinaryContentDto> getBinaryContentEntity(
            @RequestParam("binary-content-id") UUID binaryContentId) {
        if (binaryContentId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        BinaryContentDto binaryContentDto = binaryContentService.findBinaryContentById(binaryContentId);

        byte[] decodedBytes = null;
        if (binaryContentDto.bytes() != null && !binaryContentDto.bytes().isEmpty()) {
            decodedBytes = Base64.getDecoder().decode(binaryContentDto.bytes());
        }
        return ResponseEntity.ok(binaryContentDto);
    }

    @Operation(summary = "BinaryContent ID로 단일 조회", description = "PathVariable 방식으로 파일 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BinaryContentDto> getBinaryContentByPath(@PathVariable UUID id) {
        BinaryContentDto binaryContentDto = binaryContentService.findBinaryContentById(id);
        return ResponseEntity.ok(binaryContentDto);
    }

    @GetMapping("{binaryContentId}/download")
    public ResponseEntity<?> downloadBinaryContent(@PathVariable UUID binaryContentId) {
        BinaryContentDto binaryContentDto = binaryContentService.findBinaryContentById(binaryContentId);
        return binaryContentStorage.download(binaryContentDto);

    }
}
