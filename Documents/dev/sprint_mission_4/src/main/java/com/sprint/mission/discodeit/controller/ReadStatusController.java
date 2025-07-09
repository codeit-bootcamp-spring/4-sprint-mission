package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/read-status")
public class ReadStatusController {
    //의존 관계 설정
    private final ReadStatusService readStatusService;
    private final ReadStatusMapper readStatusMapper;
    private final MessageService messageService;

    public ReadStatusController(ReadStatusService readStatusService, ReadStatusMapper readStatusMapper, MessageService messageService) {
        this.readStatusService = readStatusService;
        this.readStatusMapper = readStatusMapper;
        this.messageService = messageService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/channel/{channel-id}/read-status")
    public ResponseEntity<ReadStatusResponseDto> createChannelReadStatus (@PathVariable("channel-id") UUID channelId, @RequestBody ReadStatusCreateDto dto) {
        ReadStatusResponseDto readStatus = readStatusService.create(dto);
        return new ResponseEntity<>(readStatus, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "/channel/{channel-id}/read-status")
    public ResponseEntity<ReadStatusResponseDto> updateChannelReadStatus (@PathVariable("channel-id") UUID channelId, @RequestBody ReadStatusRequestDto dto) {
        ReadStatusResponseDto readStatus = readStatusService.update(dto);
        return new ResponseEntity<>(readStatus, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{user-id}/read-status")
    public ResponseEntity<List<ReadStatusResponseDto>> getUserReadStatus (@PathVariable("user-id") UUID userId) {
        List<ReadStatusResponseDto> readStatus = readStatusService.findAllByUserId(userId);
        return new ResponseEntity<>(readStatus, HttpStatus.OK);
    }
}
