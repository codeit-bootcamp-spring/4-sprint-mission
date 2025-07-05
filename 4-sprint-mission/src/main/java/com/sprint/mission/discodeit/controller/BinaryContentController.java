package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentMapper binaryContentMapper;

    @RequestMapping(value = "/multiple", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDto>> getBinaryContents(@RequestParam UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<BinaryContentResponseDto> binaryContents =
                binaryContentService.findAllBinaryContentById(id);
        return ResponseEntity.ok(binaryContents);
    }

    @RequestMapping(value = "/single", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponseDto> getBinaryContent(@RequestParam UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        BinaryContentResponseDto binaryContentResponseDto =
                binaryContentService.findBinaryContentById(id);
        return ResponseEntity.ok(binaryContentResponseDto);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> getBinaryContentEntity(@RequestParam UUID binaryContentId) {
        if (binaryContentId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        BinaryContentResponseDto binaryContentResponseDto =
                binaryContentService.findBinaryContentById(binaryContentId);
        BinaryContent binaryContent =
                binaryContentMapper.BinaryContentResponseDtoToBinaryContent(binaryContentResponseDto);
        return ResponseEntity.ok(binaryContent);
    }
}
