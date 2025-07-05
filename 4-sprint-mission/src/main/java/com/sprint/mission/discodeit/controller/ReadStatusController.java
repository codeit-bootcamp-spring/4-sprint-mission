package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> createReadStatus(
            @RequestBody ReadStatusCreateDto readStatusCreateDto) {
        ReadStatusResponseDto readStatusResponseDto =
                readStatusService.createReadStatus(readStatusCreateDto);
        return ResponseEntity.ok(readStatusResponseDto);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(
            @RequestBody ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatusResponseDto readStatusResponseDto =
                readStatusService.updateReadStatus(readStatusUpdateDto);
        return ResponseEntity.ok(readStatusResponseDto);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusResponseDto>> getReadStatus(@RequestParam UUID channelId) {
        List<ReadStatusResponseDto> readStatusResponseDtos =
                readStatusService.findAllReadStatusByChannelId(channelId);
        return ResponseEntity.ok(readStatusResponseDtos);
    }
}
