package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/readStatus")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // [x] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
    @RequestMapping(value = "/channel", method = RequestMethod.POST)
    public ResponseEntity createReadStatus(@RequestBody ReadStatusCreateRequest request) {

        ReadStatusResponseDto responseDto = readStatusService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // [x] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
    @RequestMapping(value = "/channel/{readstatus-id}", method = RequestMethod.PUT)
    public ResponseEntity updateReadStatus(@PathVariable("readstatus-id") String readstatusId,
                                           @RequestBody ReadStatusUpdateRequest request) {

        ReadStatusResponseDto responseDto = readStatusService.update(UUID.fromString(readstatusId), request);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // [x] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
    @RequestMapping(value = "/user/{user-id}", method = RequestMethod.GET)
    public ResponseEntity getReadStatusByUser(@PathVariable("user-id") String userId) {
        List<ReadStatusResponseDto> responseDtos = readStatusService.findAllByUserId(UUID.fromString(userId));

        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }
}


