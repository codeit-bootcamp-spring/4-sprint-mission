package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "ReadStatus", description = "Message 읽음 상태 API")
public interface ReadStatusApi {

    @Operation(summary = "Message 읽음 상태 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Message 읽음 상태가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = ReadStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Channel 또는 User를 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 읽음 상태가 존재함"
            )

    })
    ResponseEntity<ReadStatusResponse> createReadStatus(
            @Parameter(description = "ReadStatus 생성 요청") ReadStatusCreateRequest request
    );

    @Operation(summary = "메시지 읽음상태 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태 수정 성공",
                    content = @Content(schema = @Schema(implementation = ReadStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Message 읽음 상태를 찾을 수 없음"
            )
    })
    ResponseEntity<ReadStatusResponse> updateReadStatus(@Parameter(description = "수정할 읽음 상태 ID") UUID readStatusId,
                                                        @Parameter(description = "수정할 읽음 상태 정보") ReadStatusUpdateRequest request);

    @Operation(summary = "User의 Message 메시지 읽음 상태 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Message 읽음 상태 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReadStatusResponse.class)))
            )
    })
    ResponseEntity<List<ReadStatusResponse>> findAllReadStatusByUserId(
            @Parameter(description = "조회할 User Id") UUID userId);
}
