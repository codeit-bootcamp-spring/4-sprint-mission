package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel", description = "Channel API")
@RestController
@RequestMapping("/api/channels")
@Slf4j
public class ChannelController {
    //의존 관계 설정
    private final ChannelService channelService;

    public ChannelController(ChannelService channelService, ChannelMapper channelMapper) {
        this.channelService = channelService;
    }

    @Operation(summary = "Public Channel 생성", description = "공개 채널을 생성합니다.", operationId = "create_3")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Public Channel이 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "id": "uuid...",
                                              "channelName": "공지사항",
                                              "description": "프로젝트 관련 공지"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/public")
    public ResponseEntity<ChannelDto> createPublicChannel(@RequestBody @Valid PublicChannelCreateDto dto) {
        log.info("공개 채널 생성 요청 수신: channelName={}", dto.getName());
        ChannelDto pubChannel = channelService.createPublicChannel(dto);
        log.info("공개 채널 생성 성공: channelName={}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(pubChannel);
    }

    @Operation(summary = "Private Channel 생성", description = "비공개 채널을 생성합니다.", operationId = "create_4")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Private Channel이 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelDto.class),
                            examples = @ExampleObject (
                                    value = """
                                             {
                                              "channelName": "비밀 공지사항",
                                              "description": "점심메뉴 추천받아요",
                                              "member": [
                                                   "123e4567-e89b-12d3-a456-426614174000",
                                                   "987f6543-e21b-65d3-b987-128615990000"
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/private")
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody @Valid PrivateChannelCreateDto dto) {
        log.info("비공개 채널 생성 요청 수신: channelName={}", dto.getName());
        ChannelDto priChannel = channelService.createPrivateChannel(dto);
        log.info("비공개 채널 생성 성공: channelName={}", dto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(priChannel);
    }

    @Operation(summary = "Channel 정보 수정", description = "Channel ID를 기반으로 채널 정보를 수정합니다(Private Channel 은 수정 불가)", operationId = "update_3")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "CHANNEL_NOT_FOUND",
                                              "message": "Channel with id {channelId} not found"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Private Channel은 수정할 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "CHANNEL_PRIVATE_UPDATE_FORBIDDEN",
                                              "message": "Private channel cannot be updated"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                                "channelId": "uuid...",
                                                "newName": "수정된 채널명",
                                                "newDescription": "설명"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("{channelId}")
    public ResponseEntity<ChannelDto> updatePublicChannel(@PathVariable UUID channelId, @RequestBody @Valid ChannelUpdateDto dto) {
        log.info("채널 정보 수정 요청 수신: channelId={}", channelId);
        ChannelDto channel = channelService.update(dto);
        log.info("채널 정보 수정 성공: channelId={}", channelId);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @Operation(summary = "Channel 삭제", description = "Channel ID를 기반으로 채널을 삭제합니다.", operationId = "delete_2")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                              "code": "CHANNEL_NOT_FOUND",
                                              "message": "Channel with id {channelId} not found"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel 성공적으로 삭제됨"
            )
    })
    @DeleteMapping("/{channelId}")
    public ResponseEntity deleteChannel(@PathVariable UUID channelId) {
        log.info("채널 정보 삭제 요청 수진: channelId={}", channelId);
        channelService.delete(channelId);
        log.info("채널 정보 삭제 성공: channelId={}", channelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", description = "해당 User ID가 참여중인 Channel 목록을 조회합니다.",operationId = "findAll_1")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    implementation = ChannelDto.class),
                            examples = @ExampleObject (
                                    value = """
                                            {
                                                     "channelId": "uuid...",
                                                     "type": "PUBLIC",
                                                     "channelName": "코코채널",
                                                     "description": "친목 채널",
                                                     "members": ["uuid1", "uuid2"],
                                                     "recentMessageTime": "2024-01-01T12:00:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<ChannelDto>> getAllPublicChannels(@RequestParam UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
