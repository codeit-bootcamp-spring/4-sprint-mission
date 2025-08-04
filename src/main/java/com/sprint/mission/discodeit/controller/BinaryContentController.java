package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
@Tag(name = "BinaryContent", description = "첨부 파일 API")
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentMapper binaryContentMapper;
    @Autowired(required = false)
    private BinaryContentStorage binaryContentStorage;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findBinaryContents(@Parameter(description = "조회할 첨부 파일 ID 목록") @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        List<BinaryContentDto> response = binaryContents.stream().map(binaryContentMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{binary-content-id}")
    public ResponseEntity findBinaryContent(@Parameter(description = "조회할 첨부 파일 ID") @PathVariable("binary-content-id") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto response = binaryContentMapper.toDto(binaryContent);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{binaryContentId}/download")
    public ResponseEntity downloadBinaryContent(@Parameter(schema = @Schema(format = "uuid")) @PathVariable("binaryContentId") UUID binaryContentId) throws IOException {
        if (binaryContentStorage == null)
            throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_STORAGE_NOT_FOUND);

        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContent);
        ResponseEntity<Resource> response = binaryContentStorage.download(binaryContentDto);
        return response;
    }
}
