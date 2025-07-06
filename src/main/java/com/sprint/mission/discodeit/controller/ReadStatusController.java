package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> createReadStatus(@RequestBody ReadStatusCreateRequest request) {
        ReadStatus readStatus = readStatusService.create(request);
        ReadStatusDto readStatusDto = readStatusService.makeDto(readStatus);
        return new ResponseEntity<>(readStatusDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{read-status-id}", method = RequestMethod.PATCH)
    public ResponseEntity<ReadStatusDto> patchReadStatus(@PathVariable("read-status-id") UUID readStatusId, @RequestBody ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusService.update(readStatusId, request);
        ReadStatusDto readStatusDto = readStatusService.makeDto(readStatus);
        return new ResponseEntity<>(readStatusDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> getReadStatusByUserId(@PathVariable("user-id") UUID userId) {
        List<ReadStatus> readStatusList = readStatusService.findAllByUserId(userId);
        List<ReadStatusDto> readStatusDtos = new ArrayList<>();
        for (ReadStatus readStatus : readStatusList) {
            readStatusDtos.add(readStatusService.makeDto(readStatus));
        }
        return new ResponseEntity<>(readStatusDtos, HttpStatus.OK);
    }
}
