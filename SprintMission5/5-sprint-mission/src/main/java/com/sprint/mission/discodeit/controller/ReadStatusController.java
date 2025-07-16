package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatus;
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
  public ResponseEntity<ReadStatus> createReadStatus(
      @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {
    ReadStatus readStatus =
        readStatusService.createReadStatus(readStatusCreateRequest);
    return ResponseEntity.ok(readStatus);
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> updateReadStatus(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest updateRequest) {
    ReadStatus updated = readStatusService.updateReadStatus(readStatusId, updateRequest);
    return ResponseEntity.ok(updated);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatus>> getReadStatus(
      @RequestParam UUID userId) {
    List<ReadStatus> readStatuses =
        readStatusService.findAllReadStatusByUserId(userId);
    return ResponseEntity.ok(readStatuses);
  }
}
