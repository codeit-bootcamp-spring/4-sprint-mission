package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 바이너리 파일을 1개 또는 여러개 조회할 수 있다. Get
    @GetMapping("/binary-id")
    public ResponseEntity<BinaryContentDto> searchBinaryFile(@PathVariable("binary-id") UUID binaryId,
                                                             BinaryContentRequest binaryContentRequest) {
        BinaryContentDto binaryFile = binaryContentService.searchBinaryContent(binaryId, binaryContentRequest);
        return ResponseEntity.ok(binaryFile);
    }

}
