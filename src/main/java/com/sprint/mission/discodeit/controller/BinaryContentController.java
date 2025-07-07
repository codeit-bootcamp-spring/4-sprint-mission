package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/binary-contents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    // 단일 조회
    @RequestMapping(method = RequestMethod.GET, value = "/{binary-content-id}")
    public ResponseEntity<BinaryContentResponseDto> getBinaryContent(@PathVariable("binary-content-id") UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    // 여러 개 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDto>> getBinaryContents(@RequestParam("binary-content-ids") List<UUID> binaryContentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    /*
     * DELETE 는 요구사항에 없었지만 추가해놓았습니다
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{binary-content-id}")
    public ResponseEntity<BinaryContentResponseDto> deleteBinaryContent(@PathVariable("binary-content-id") UUID binaryContentId) {
        binaryContentService.delete(binaryContentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
