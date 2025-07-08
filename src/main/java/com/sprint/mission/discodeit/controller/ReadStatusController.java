package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/read-status")
public class ReadStatusController {

    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보를 생성할 수 있다. Post
    @PostMapping
    public ResponseEntity<ReadStatusDto> createReadStatus(
            @RequestBody ReadStatusRequest readStatusRequest) {
        ReadStatusDto readStatusDto = readStatusService.createReadStatus(readStatusRequest);
        return ResponseEntity.ok().body(readStatusDto); // post는 body가 있고 get은 body가 없다
    }
    // 특정 채널의 메시지 수신 정보를 수정할 수 있다. Patch
    @PatchMapping("/{read-status-id}")
    public ResponseEntity<ReadStatusDto> updateReadStatus(
            @PathVariable("read-status-id") UUID readStatusId,
            @RequestBody ReadStatusRequest readStatusRequest
    ) {
        ReadStatusDto updateReadStatus = readStatusService.updateReadStatus(readStatusId, readStatusRequest);
        return ResponseEntity.ok(updateReadStatus);
    }
    // 특정 사용자의 메시지 수신 정보를 조회할 수 있다 .Get
    // 특정 사용자의 메시지 수신 정보니까 사용자는 1명으로 특정하고 메시지 수신 정보가 여러개일 수 있을 것이다
    // 그러니 List?
    // Get이니까 ResponseBody는 필요없을것이다.
    @GetMapping("/{user-id}")
    public ResponseEntity<List<ReadStatusDto>> findReadStatus(@PathVariable("user-id") UUID userId) {
        List<ReadStatusDto> findReadStatus = readStatusService.findReadStatus(userId);
        return ResponseEntity.ok(findReadStatus);
    }
}
