package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1")
@Validated
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    public ReadStatusController(ReadStatusService readStatusService) {
        this.readStatusService = readStatusService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ReadStatusResponseDto> createReadStatus(@RequestBody @Valid ReadStatusCreateDto dto) {
        ReadStatusResponseDto created = readStatusService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{read-status-id}")
    public ResponseEntity<ReadStatusResponseDto> getReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        return ResponseEntity.ok(readStatusService.find(readStatusId));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{read-status-id}")
    public ResponseEntity<ReadStatusResponseDto> updateReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        return ResponseEntity.ok(readStatusService.update(new ReadStatusUpdateDto(readStatusId)));
    }

    /*
    * DELETE 는 요구사항에 없었지만 추가해놓았습니다
    */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{read-status-id}")
    public ResponseEntity<ReadStatusResponseDto> deleteReadStatus(@PathVariable("read-status-id") UUID readStatusId) {
        readStatusService.delete(readStatusId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
