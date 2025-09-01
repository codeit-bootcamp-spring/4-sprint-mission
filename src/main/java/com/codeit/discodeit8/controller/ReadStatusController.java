package com.codeit.discodeit8.controller;

import com.codeit.discodeit8.dto.user_service_dto.UserDto;
import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusCreateRequest;
import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit8.dto.readstatus_dto.ReadStatusUpdateRequest;
import com.codeit.discodeit8.service.ReadStatusService;
import com.codeit.discodeit8.service.UserService;
import com.codeit.discodeit8.swagger.SwaggerReadStatusController;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
@RequestMapping("/api/readStatuses")
public class ReadStatusController implements SwaggerReadStatusController {

  private final UserService userService;
  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @Valid @RequestBody ReadStatusCreateRequest readStatusCreateRequest) {

    ReadStatusDto readStatusDto = readStatusService
        .findReadStatusByUserIdAndChannelId(readStatusCreateRequest.getUserId(),
            readStatusCreateRequest.getChannelId());

    return ResponseEntity
        .status(HttpStatus.CREATED) // 201 Created
        .body(readStatusDto);
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> findAllByUserId(@RequestParam UUID userId) {
    UserDto user = userService.findUserDtoByUserId(userId);
    List<ReadStatusDto> readStatusDtoList =
        readStatusService.findReadStatuseDtoListByUserId(user.id());

    return ResponseEntity.ok(readStatusDtoList);
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> update(
      @PathVariable("readStatusId") UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest readStatusUpdateRequest) {
    ReadStatusDto updateReadStatusDto = readStatusService.updateReadStatusByReadStatusId(
        readStatusId, readStatusUpdateRequest);

    return ResponseEntity.ok(updateReadStatusDto);
  }
}