package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent",description = "바이너리 파일 관련 API")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    @Operation(summary = "단일 바이너리 파일 조회", description = "바이너리 파일을 한 개 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회에 성공했습니다")
    @ApiResponse(responseCode = "404",description = "해당하는 바이너리 파일이 없습니다")
    public ResponseEntity<BinaryContent> getSingleContent(
            @Parameter(description = "조회하려는 바이너리 파일 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("binaryContentId") UUID binaryContentId) {
        com.sprint.mission.discodeit.entity.BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContent binaryContentDto = binaryContentService.makeDto(binaryContent);
        return ResponseEntity.ok(binaryContentDto);
    }

    //GET /binary-content?ids=uuid1,uuid2,uuid3...
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "바이너리 파일 다건 조회")
    @ApiResponse(responseCode = "200", description = "조회에 성공했습니다")
    public ResponseEntity<List<BinaryContent>> getAllContentByIdIn(@RequestParam(required = true) List<UUID> ids) {
        List<com.sprint.mission.discodeit.entity.BinaryContent> binaryContents = binaryContentService.findAllByIdIn(ids);
        List<BinaryContent> binaryContentDtos = binaryContents.stream()
                .map(binaryContentService::makeDto)
                .toList();
        return ResponseEntity.ok(binaryContentDtos);

    }

}


