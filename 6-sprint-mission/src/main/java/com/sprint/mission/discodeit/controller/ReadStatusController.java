package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/readStatuses")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @PostMapping
    public ResponseEntity<ReadStatusDto> createReadStatus(
            @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
        ReadStatusDto readStatusDto = readStatusService.createReadStatus(readStatusCreateRequest);
        return ResponseEntity.ok(readStatusDto);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateReadStatus(
            @PathVariable UUID readStatusId, @RequestBody ReadStatusUpdateRequest updateRequest) {
        ReadStatusDto updated = readStatusService.updateReadStatus(readStatusId, updateRequest);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<ReadStatusDto>> getReadStatus(@RequestParam UUID userId) {
        List<ReadStatusDto> readStatusDtos = readStatusService.findAllReadStatusByUserId(userId);
        return ResponseEntity.ok(readStatusDtos);
    }
}
