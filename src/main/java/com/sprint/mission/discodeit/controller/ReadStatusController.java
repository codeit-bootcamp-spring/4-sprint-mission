package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ReadStatusController {
    private final ReadStatusService readStatusService;

    // 특정 채널의 메시지 수신 정보 생성
    @RequestMapping(value = "channels/{channel-id}/readstatus", method = RequestMethod.POST)
    public ResponseEntity<ReadStatusDto> createReadStatusByChannelId(@PathVariable("channel-id") UUID channelId,
                                                                     @RequestBody ReadStatusCreateRequest request) {
        System.out.println("######### createMessageStatusByChannelId");
        System.out.println("# request =" + request);

        return ResponseEntity.ok(readStatusService.create(request));
    }

    // 특정 채널의 메시지 수신 정보 수정
    @RequestMapping(value = "channels/{channel-id}/readstatus", method = RequestMethod.PATCH)
    public ResponseEntity<List<ReadStatusDto>> updateReadStatusByChannelId(@PathVariable("channel-id") UUID channelId,
                                                                           @RequestBody ReadStatusUpdateRequest request) {
        System.out.println("######### updateMessageStatusByChannelId");
        System.out.println("# request =" + request);

        return ResponseEntity.ok(readStatusService.updateByChannelId(channelId, request));
    }

    // 특정 사용자의 메시지 수신 정보 조회
    @RequestMapping(value = "users/{user-id}/readstatus", method = RequestMethod.GET)
    public ResponseEntity<List<ReadStatusDto>> findAllReadStatusByUserId(@PathVariable("user-id") UUID userId) {
        System.out.println("######### findAllMessageStatusByUserId");
        System.out.println("# userId =" + userId);

        return ResponseEntity.ok(readStatusService.findAllByUserId(userId));
    }
}
