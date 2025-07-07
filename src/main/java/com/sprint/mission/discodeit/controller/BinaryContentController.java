package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/binarycontents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 바이너리 파일 1건 조회
    @RequestMapping(value = "/{binary-content-id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> getBinaryContentById(@PathVariable("binary-content-id") UUID binaryContentId) {
        System.out.println("######### getBinaryContentById");
        System.out.println("# binary-content-id = " + binaryContentId);

        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    // 바이너리 파일 다건 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> getBinaryContentsByIds(@RequestParam List<UUID> binaryContentIds) {
        System.out.println("######### getBinaryContentsByIds");

        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<UUID>> getAllBinaryContentIds() {
        return ResponseEntity.ok(binaryContentService.findAllIds());
    }

}
