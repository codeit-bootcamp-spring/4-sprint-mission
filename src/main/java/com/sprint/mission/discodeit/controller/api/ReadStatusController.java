package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatus")
@RequiredArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createReadStatus(@RequestBody @Valid ReadStatusDto.create request) {
        ReadStatus createdReadStatus = readStatusService.create(request);
        ReadStatusDto.response response = createdReadStatus.toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{channel-id}")
    public ResponseEntity<?> updateReadStatusByChannelId(@PathVariable("channel-id") UUID channelId) {
        List<ReadStatus> updatedReadStatuses = readStatusService.updateByChannelId(channelId);
        List<ReadStatusDto.response> response = updatedReadStatuses.stream().map(ReadStatus::toDto).toList();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{user-id}")
    public ResponseEntity<?> getReadStatusesByUserId(@PathVariable("user-id") UUID userId) {
        List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
        List<ReadStatusDto.response> response = readStatuses.stream().map(ReadStatus::toDto).toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
