package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binary-content-id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto> getSingleContent(@PathVariable("binary-content-id") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentService.makeDto(binaryContent);
        return ResponseEntity.ok(binaryContentDto);
    }

    //GET /binary-content?ids=uuid1,uuid2,uuid3...
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto>> getAllContentByIdIn(@RequestParam(required = true) List<UUID> ids) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(ids);
        List<BinaryContentDto> binaryContentDtos = binaryContents.stream()
                .map(binaryContentService::makeDto)
                .toList();
        return ResponseEntity.ok(binaryContentDtos);

    }

}


