package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/readStatus")
@Tag(name = "메시지 읽음 상태 (ReadStatus)", description = "메시지 읽음 상태 조회 및 생성, 수정과 관련된 API입니다.")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @Operation(summary = "새로운 메시지 읽음 상태를 생성합니다.")
  @PostMapping
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @Operation(summary = "특정 메시지 읽음 상태를 수정합니다.")
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> update(
      @Parameter(description = "수정할 읽음 상태의 고유 ID")
      @PathVariable("readStatusId") UUID readStatusId,

      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @Operation(summary = "특정 사용자의 메시지 읽음 상태 목록을 조회합니다.")
  @GetMapping
  public ResponseEntity<List<ReadStatus>> findAllByUserId(
      @Parameter(description = "읽음 상태를 조회할 사용자의 고유 ID")
      @RequestParam("userId") UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
