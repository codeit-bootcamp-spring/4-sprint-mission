package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/binaryContents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // [x] 바이너리 파일을 1개 조회할 수 있다.
    @RequestMapping(value = "/{content-id}", method = RequestMethod.GET)
    public ResponseEntity getSingleBinaryContent(@PathVariable("content-id") String contentId) {

        BinaryContentResponseDto binaryContentResponseDto = binaryContentService.find(UUID.fromString(contentId));

        return ResponseEntity.status(HttpStatus.OK).body(binaryContentResponseDto);
    }

    // [x] 바이너리 파일 여러 개를 조회할 수 있다.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getMultipleBinaryContents(@RequestBody BinaryContentRequest request) {

        List<BinaryContentResponseDto> responseDtos = binaryContentService.findAllByIdIn(request);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }
}
