package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ChannelDto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/channels")
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {

    private final ChannelService channelService;

    //공개 채널 생성
    @Operation(summary = "Public Channel 생성", operationId = "create_3")
    @ApiResponse(
            responseCode = "201",
            description = "public Channel이 성공적으로 생성됨",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = ChannelResponse.class))
    )
    @PostMapping(value = "/public")
    public ResponseEntity<ChannelResponse> createPublicChannel (
                                                                @Parameter(description = "Public Channel 생성 정보")
                                                                @RequestBody PublicChannelCreateRequest dto) {
        ChannelResponse publicChannel = channelService.createPublicChannel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(publicChannel);
    }

    //비공개 채널 생성
    @Operation(summary = "Private Channel 생성", operationId = "create_4")
    @ApiResponse(
            responseCode = "201",
            description = "Private Channel이 성공적으로 생성됨",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = ChannelResponse.class),
                    examples = @ExampleObject(value = "")
            )
    )
    @PostMapping(value = "/private")
    public ResponseEntity<ChannelResponse> createPrivateChannel (
                                                                @Parameter(description = "Private Channel 생성 정보")
                                                                @RequestBody PrivateChannelCreateRequest dto) {
        ChannelResponse privateChannel = channelService.createPrivateChannel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(privateChannel);
    }

/*================================================================================================================*/

    //특정 사용자가 볼 수 있는 모든 채널 목록 조회
    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "findAll_1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공",
                    content = @Content(mediaType = "*/*", array = @ArraySchema(
                            schema = @Schema(implementation = UserChannelResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<UserChannelResponse>> findAllChannels(@Parameter(description = "조회할 User ID")
                                                                     @RequestParam("userId") UUID userId) {
        List<UserChannelResponse> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.ok(channels);
    }

    //공개 채널의 정보 수정
    @Operation(summary = "공개 채널 정보 수정", operationId = "update_3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공개 채널 수정 성공",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = ChannelResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "수정불가한 Private Channel",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "Private channel cannot be updated"))
            ),
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "Channel with id {channelId} not found"))
            )}
    )
    @PatchMapping(value = "/{channelId}")
    public ResponseEntity<ChannelResponse> updatePublicChannel(
                                                                @Parameter(description = "수정할 Channel ID")
                                                                @PathVariable("channelId") UUID channelId,
                                                                @RequestBody PublicChannelUpdateRequest request) {

        ChannelResponse updatedChannel = channelService.update(channelId, request);
        return ResponseEntity.ok(updatedChannel);
    }

    //채널 삭제
    @Operation(summary = "채널 삭제", description = "채널을 삭제합니다. 비공개 및 공개 채널 모두 해당됩니다.", operationId = "delete_2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "채널 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "채널을 찾을 수 없음",
                    content = @Content(mediaType = "*/*",
                            examples = @ExampleObject(value = "{\"message\": \"Channel with id {channelId} not found\"}")))
    })
    @DeleteMapping(value = "/{channelId}")
    public ResponseEntity deleteChannel(@PathVariable("channelId") UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.noContent().build();
    }
}
