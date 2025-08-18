package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Validated
public interface ChannelApi {
    @Operation(summary = "Public Channel 생성", operationId = "create_3", responses = {
            @ApiResponse(responseCode = "201", description = "Public Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ChannelDto.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity createPublicChannel(@Valid @RequestBody PublicChannelCreateRequest publicChannelCreateRequest);

    @Operation(summary = "Private Channel 생성", operationId = "create_4", responses = {
            @ApiResponse(responseCode = "201", description = "Private Channel이 성공적으로 생성됨", content = @Content(schema = @Schema(implementation = ChannelDto.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequest privateChannelCreateRequest);

    @Operation(summary = "Channel 정보 수정", operationId = "update_3", responses = {
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel with id not found"))),
            @ApiResponse(responseCode = "400", description = "Private Channel은 수정할 수 없음", content = @Content(examples = @ExampleObject(value = "Private channel cannot be updated"))),
            @ApiResponse(responseCode = "200", description = "Channel 정보가 성공적으로 수정됨", content = @Content(schema = @Schema(implementation = ChannelDto.class)))
    })
    ResponseEntity updatePublicChannel(@Parameter(description = "수정할 Channel ID")
                                       @NotNull(message = "잘못된 ID 입니다.")
                                       @PathVariable("channel-id") UUID channelId,
                                       @Valid @RequestBody PublicChannelUpdateRequest publicChannelUpdateRequest);

    @Operation(summary = "Channel 삭제", operationId = "delete_2", responses = {
            @ApiResponse(responseCode = "404", description = "Channel을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = "Channel with id not found"))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation"))),
            @ApiResponse(responseCode = "204", description = "Channel이 성공적으로 삭제 됨", content = @Content())
    })
    ResponseEntity deleteChannel(@Parameter(description = "삭제할 Channel ID")
                                 @NotNull(message = "잘못된 ID 입니다.")
                                 @PathVariable("channel-id") UUID channelId);

    @Operation(summary = "User가 참여 중인 Channel 목록 조회", operationId = "finnAll_1", responses = {
            @ApiResponse(responseCode = "200", description = "Channel 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChannelDto.class)))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력 및 검증 실패", content = @Content(examples = @ExampleObject(value = "Invalid request body | Constraint violation")))
    })
    ResponseEntity findChannelByUserId(@Parameter(description = "조회할 User ID")
                                       @NotNull(message = "잘못된 ID 입니다.")
                                       @RequestParam("userId") UUID userId);
}
