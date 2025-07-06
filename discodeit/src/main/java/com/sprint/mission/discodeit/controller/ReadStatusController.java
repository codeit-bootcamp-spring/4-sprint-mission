package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/read-status")
@RequiredArgsConstructor
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    //특정 채널의 수신 정보 생성
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatusResponseDto> create(@ModelAttribute ReadStatusCreateDto dto) {
        ReadStatusResponseDto created = readStatusService.create(dto);
        return ResponseEntity.ok(created);
    }

    //특정 채널의 수신 정보 수정
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ReadStatusResponseDto> update(@RequestBody ReadStatusDto dto) {
        ReadStatusResponseDto updated = readStatusService.update(dto);
        return ResponseEntity.ok(updated);
    }

    //특정 사용자의 메시지 수신 정보 전체 조회
    @RequestMapping(value = "/{user-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ReadStatusResponseDto>> findByUserId(@PathVariable("user-id") UUID userId) {
        List<ReadStatusResponseDto> statuses = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(statuses);
    }
}
