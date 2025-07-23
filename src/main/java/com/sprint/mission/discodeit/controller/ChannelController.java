package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Channel", description = "Channel API")
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;

    @Operation(summary = "Public Channel 생성", operationId = "create_3", responses = {
            @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ChannelPublicCreateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.POST, value = "/public")
    public ResponseEntity createPublicChannel(@RequestBody PublicChannelCreateRequest publicChannelCreateRequest) {
        ChannelPublicCreateResponseDto response = channelService.createPublicChannel(publicChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Private Channel 생성", operationId = "create_4", responses = {
            @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ChannelPrivateCreateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.POST, value = "/private")
    public ResponseEntity createPrivateChannel(@RequestBody PrivateChannelCreateRequest privateChannelCreateRequest) {
        ChannelPrivateCreateResponseDto response = channelService.createPrivateChannel(privateChannelCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Channel 정보 수정", operationId = "update_3", responses = {
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel with id not found"))),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음", content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ChannelResponseDto.class)))
    })
    @RequestMapping(method = RequestMethod.PATCH, value = "/{channel-id}")
    public ResponseEntity updatePublicChannel(@Parameter(description = "수정할 Channel ID") @PathVariable("channel-id") UUID channelId, @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest) {
        ChannelResponseDto response = channelService.updateChannel(channelId, publicChannelUpdateRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Channel 삭제", operationId = "delete_2", responses = {
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation"))),
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제 됨", content = @Content())
    })
    @RequestMapping(method = RequestMethod.DELETE, value = "/{channel-id}")
    public ResponseEntity deletePublicChannel(@Parameter(description = "삭제할 Channel ID") @PathVariable("channel-id") UUID channelId) {
        channelService.deleteChannel(channelId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "finnAll_1", responses = {
            @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findChannelByUserId(@Parameter(description = "조회할 User ID") @RequestParam("userId") UUID userId) {
        List<ChannelDto> responses = channelService.findAllByUserId(userId);

        return ResponseEntity.ok(responses);
    }
}
