package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find")
    public ResponseEntity<BinaryContent> getBinaryContent (@RequestParam UUID binaryContentId) {
        BinaryContent dto = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(dto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/multiple" )
    public ResponseEntity<List<BinaryContentResponseDto>> getBinaryContents (@RequestParam List<UUID> fileIds) {
        List<BinaryContentResponseDto> files = binaryContentService.findAllByIdIn(fileIds);
        return ResponseEntity.ok(files);
    }
}
