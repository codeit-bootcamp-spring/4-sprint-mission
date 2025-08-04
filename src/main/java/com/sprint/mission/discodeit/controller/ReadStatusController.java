package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public class ReadStatusController implements ReadStatusApi {
    private final ReadStatusService readStatusService;
    private final ReadStatusMapper readStatusMapper;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findALlByUserId(@Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        List<ReadStatusDto> response = readStatuses.stream().map(readStatusMapper::toDto).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createReadStatus(@RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatus readStatus = readStatusService.create(readStatusCreateRequest);
        ReadStatusDto response = readStatusMapper.toDto(readStatus);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/{read-status-id}")
    public ResponseEntity updateReadStatus(@Parameter(description = "수정할 읽음 상태 ID") @PathVariable("read-status-id") UUID readStatusId, @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = readStatusService.update(readStatusId, readStatusUpdateRequest);
        ReadStatusDto response = readStatusMapper.toDto(readStatus);

        return ResponseEntity.ok(response);
    }
}
