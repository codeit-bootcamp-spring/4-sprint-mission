package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "Channel", description = "Channel API")
public interface ChannelApi {

    @Operation(summary = "Public Channel 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Public Channel이 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            )
    })
    ResponseEntity<ChannelResponse> createPublicChannel(
            @Parameter(
                    description = "Public Channel 생성 요청") ChannelCreateRequest request
    );


    @Operation(summary = "Private Channel 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Private Channel이 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            )
    })
    ResponseEntity<ChannelResponse> createPrivateChannel(
            @Parameter(
                    description = "Private Channel 생성 요청") PrivateChannelCreateRequest request
    );


    @Operation(summary = "Channel 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 정보가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel을 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Private Channel은 수정할 수 없음"
            )
    })
    ResponseEntity<ChannelResponse> updateChannel(
            @Parameter(description = "수정할 Channel ID") UUID channelId,
            @Parameter(description = "수정할 Channel 정보") ChannelUpdateRequest request
    );


    @Operation(summary = "Channel 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel을 찾을 수 없음"
            )
    })
    ResponseEntity<ChannelResponse> findChannelById(@Parameter(description = "Channel Id") UUID channelId);


    @Operation(summary = "User가 참여 중인 Channel 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Channel 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ChannelResponse.class))
            )
    })
    ResponseEntity<List<ChannelResponse>> findAllChannelsByUserId(@Parameter(description = "유저 Id") UUID userId);


    @Operation(summary = "Channel 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Channel을 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "404", description = "Channel not found"
            )
    })
    ResponseEntity<Void> deleteChannel(@Parameter(description = "삭제할 Channel Id") UUID channelId);
}
