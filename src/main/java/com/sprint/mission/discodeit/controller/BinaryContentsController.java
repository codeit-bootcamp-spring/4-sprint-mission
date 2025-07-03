package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/discodeit/binaryContent")// 요수항에 URL 지시사항 잉 있어 카멜케이스 안씀
public class BinaryContentsController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDto>> findAllBinaryContents() throws IOException {
        List<BinaryContentResponseDto> binaryContentResponseDtoList = binaryContentService.findAllBinaryContentDtos();
        return ResponseEntity.ok(binaryContentResponseDtoList);
    }

    @RequestMapping(value = "/{binary-contents-id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponseDto> findBinaryContentById(
            @PathVariable("binary-contents-id") UUID binaryContentsId) throws IOException {

        BinaryContentResponseDto dto = binaryContentService.findBinaryContentDtoByBinaryContentId(binaryContentsId);
        return ResponseEntity.ok(dto);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET) // 요수항에 URL 지시사항 잉 있어 카멜케이스 안씀
    public ResponseEntity<BinaryContent> findBinaryContentByBinaryContentsId(@RequestParam UUID binaryContentId) throws IOException {
        BinaryContent binaryContent = binaryContentService.findBinaryContentByBinaryContentId(binaryContentId);
        return ResponseEntity.ok(binaryContent);
    }

}