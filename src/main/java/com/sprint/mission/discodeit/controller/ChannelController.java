package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.ChannelApi;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
 * 기존: /v1/channels, PathVariable: channel-id
 * 요구사항: /api/channels, PathVariable: channelId
 * 요구사항에 맞춰 변경하였습니다.
 *
 * CommonResponse를 만들고 스웨거 문서에 반영도 했습니다.
 * 하지만 코드잇에서 주어진 API 스펙과 프론트엔드 코드와 맞지 않아 모두 제거했습니다.
 * 그래서 CommonResponse가 쓰인 곳은 없지만, CommonResponse를 비롯한 에러를 담은 응답 코드에 대한 피드백을 받으면 좋을 것 같아서 함께 올립니다.
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController implements ChannelApi {
    private final ChannelService channelService;

    /*
    * 기존: POST /v1/channels => body의 channel-type 값에 따라 분기 처리하여 비공개/공개 채널 생성
    * 요구사항: POST /api/channels/private, POST /api/channels/public => 핸들러 메서드 분리하여 채널 생성
    * */
    @PostMapping("/public")
    public ResponseEntity<ChannelResponseDto> createPublicChannel(@RequestBody @Valid ChannelCreateDto dto) {
        ChannelResponseDto created = channelService.createPublicChannel(dto);
        dto.setChannelType(ChannelType.PUBLIC);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PostMapping("/private")
    public ResponseEntity<ChannelResponseDto> createPrivateChannel(@RequestBody @Valid ChannelCreateDto dto) {
        dto.setChannelType(ChannelType.PRIVATE);
        ChannelResponseDto created = channelService.createPrivateChannel(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    /*
    * 이전에는 채널 목록 조회를 위해 유저의 아이디를 헤더에 담아 전달했으나,
    * 프론트엔드 코드(= 코드잇)에 맞추기 위해서 RequestParam으로 변경했습니다.*/
    @GetMapping
    public ResponseEntity<List<ChannelResponseDto>> getChannels(@RequestParam("userId") UUID userId) {
        return ResponseEntity
                .ok(channelService.findAllByUserId(userId));
    }

    @PatchMapping("/{channelId}")
    public ResponseEntity<ChannelResponseDto> updateChannel(@PathVariable("channelId") UUID channelId,
                                                          @RequestBody @Valid ChannelUpdateDto dto) {
        ChannelResponseDto updated = channelService.update(channelId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
