package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
@Tag(name = "Channel", description = "채널 관련 API")
public class ChannelController {

    private final ChannelService channelService;
    private final ReadStatusService readStatusService;

    @Operation(summary = "신규 비공개 채널 생성", description = "신규 비공개 채널을 생성합니다")
    @ApiResponse(responseCode = "201", description = "비공개 채널 생성 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @RequestMapping(value = "/private",method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto channelDto = channelService.makeDto(channel);

        return new ResponseEntity<>(channelDto, HttpStatus.CREATED);
    }

    @Operation(summary = "신규 공개 채널 생성", description = "신규 공개 채널을 생성합니다")
    @ApiResponse(responseCode = "201", description = "공개 채널 생성 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @RequestMapping(value = "/public",method = RequestMethod.POST)
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        Channel channel = channelService.create(request);
        ChannelDto channelDto = channelService.makeDto(channel);
        return new ResponseEntity<>(channelDto, HttpStatus.CREATED);
    }

    @Operation(summary = "채널 수정", description = "기존 채널 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "채널 수정 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다")
    @RequestMapping(value = "/{channelId}",method = RequestMethod.PATCH)
    public ResponseEntity<ChannelDto> patchPublicChannel(
            @Parameter(description = "수정할 채널 UUID",example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("channelId") UUID channelId, @RequestBody PublicChannelUpdateRequest request) {
        Channel updatedChannel =  channelService.update(channelId, request);
        ChannelDto channelDto = channelService.makeDto(updatedChannel);
        return new ResponseEntity<>(channelDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{channelId}",method = RequestMethod.DELETE)
    @Operation(summary = "채널 삭제", description = "기존 채널을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "채널 삭제 완료")
    @ApiResponse(responseCode = "400", description = "잘못된 UUID 형식 입니다")
    @ApiResponse(responseCode = "404", description = "해당 채널을 찾을 수 없습니다")
    public ResponseEntity<Void> deletePublicChannel(
            @Parameter(description = "삭제하려는 채널 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "유저 공개 채널 목록 조회",description = "해당 유저가 참여하고 있는 공개 채널 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회에 성공했습니다.")
    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public ResponseEntity<List<ChannelDto>> getPublicChannelByUserId(
            @Parameter(description = "조회하고자 하는 유저 UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @RequestParam("userId") UUID userId) {
        List<ChannelDto> foundChannel = channelService.findAllByUserId(userId);
        return new ResponseEntity<>(foundChannel, HttpStatus.OK);
    }


}
