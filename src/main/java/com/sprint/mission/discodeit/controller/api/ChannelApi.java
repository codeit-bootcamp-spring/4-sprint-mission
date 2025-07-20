package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@Tag(name = "🚪Channel", description = "채널 관련 API")
public interface ChannelApi {
    @Operation(summary = "공개 채널 생성", description = "공개 채널을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "PUBLIC 채널 생성 DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelCreateDto.class),
                            examples = @ExampleObject(
                                    name    = "PUBLIC 생성 예시",
                                    summary = "공개 채널 생성",
                                    value   = """
                                                  {
                                                    "name": "❓학습QnA📖",
                                                    "description": "❓학습QnA📖 채널의 시작이에요. 학습 질의응답을 위한 채널이에요. 학습 내용에 관한 질문이 있다면, 이 채널에 올려주세요."
                                                  }
                                              """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정상적으로 생성되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example(PUBLIC)",
                                    summary = "공개 채널 생성 성공 예시",
                                    value = """
                                                {
                                                    "id": "dc464245-f51a-4920-a841-f45cda0435b2",
                                                    "type": "PUBLIC",
                                                    "name": "❓학습QnA📖",
                                                    "description": "❓학습QnA📖 채널의 시작이에요. 학습 질의응답을 위한 채널이에요.  학습 내용에 관한 질문이 있다면, 이 채널에 올려주세요.",
                                                    "participantIds": [],
                                                    "lastMessageSentAt": "2025-07-20T15:28:06.858726Z"
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name    = "BadRequest - 유효성 검증 실패",
                                            summary = "필드 유효성 검사에 실패한 경우",
                                            value   = """
                                                        {
                                                          "success": false,
                                                          "code": 400,
                                                          "message": "Bad Request Exception",
                                                          "data": "Public 채널 생성 시 name 이 필요합니다.",
                                                          "timestamp": "2025-07-09T17:16:31.902853"
                                                        }
                                                      """
                                    ),
                                    @ExampleObject(
                                            name    = "BadRequest - 중복 채널",
                                            summary = "이미 존재하는 채널명인 경우(PUBLIC)",
                                            value   = """
                                                        {
                                                          "success": false,
                                                          "code": 400,
                                                          "message": "Bad Request Exception",
                                                          "data": "이미 사용 중인 채널 이름입니다: 개발 기록 챌린지",
                                                          "timestamp": "2025-07-09T17:15:48.807846"
                                                      }
                                                      """
                                    )
                            }
                    ))
    })
    ResponseEntity<ChannelResponseDto> createPublicChannel(ChannelCreateDto dto);


    @Operation(summary = "비공개 채널 생성", description = "비공개 채널을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비공개 채널 생성 DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChannelCreateDto.class),
                            examples = @ExampleObject(
                                    name    = "PRIVATE 생성 예시",
                                    summary = "비공개 채널 생성",
                                    value   = """
                                                    {
                                                        "participantIds": [
                                                            "b2679878-5a10-41a3-b4b0-008ae83da8dd",
                                                            "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4",
                                                            "82170d94-e9e6-45df-874a-f4fb949f0835"
                                                            ]
                                                   }
                                              """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정상적으로 생성되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example(PRIVATE)",
                                    summary = "비공개 채널 성공 예시",
                                    value = """
                                                {
                                                    "id": "395b0bd9-a145-499f-ae6b-6bbacbf65651",
                                                    "type": "PRIVATE",
                                                    "name": null,
                                                    "description": null,
                                                    "participantIds": [
                                                        "b2679878-5a10-41a3-b4b0-008ae83da8dd",
                                                        "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4",
                                                        "82170d94-e9e6-45df-874a-f4fb949f0835"
                                                    ],
                                                    "lastMessageSentAt": "2025-07-20T15:21:53.078720Z"
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name    = "BadRequest - 유효성 검증 실패",
                                            summary = "필드 유효성 검사에 실패한 경우",
                                            value   = """
                                                            {
                                                              "success": false,
                                                              "code": 400,
                                                              "message": "Bad Request Exception",
                                                              "data": "Public 채널 생성 시 name 이 필요합니다.",
                                                              "timestamp": "2025-07-09T17:16:31.902853"
                                                            }
                                                      """
                                    )
                            }
                    ))
    })
    ResponseEntity<ChannelResponseDto> createPrivateChannel(ChannelCreateDto dto);


    @Operation(summary = "채널 목록 조회", description = "유저 아이디(userId)가 참여하고 있는 전체 채널을 조회합니다.",
            parameters = @Parameter(
                    name        = "userId",
                    in          = ParameterIn.QUERY,
                    description = "사용자 ID (UUID)",
                    required    = true,
                    example     = "82170d94-e9e6-45df-874a-f4fb949f0835"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 조회되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "성공 예시",
                                    value = """
                                                [
                                                    {
                                                        "id": "270d615f-8b0d-41a8-a3bc-1e008d6df0e2",
                                                        "type": "PUBLIC",
                                                        "name": "📒학습-공지",
                                                        "description": "#📒학습-공지 채널의 시작이에요. 학습 관련 사항이 공지되는 채널이에요. 이 채널에 공유되는 소식은 모든 멤버가 꼭 확인해 주세요!",
                                                        "participantIds": [],
                                                        "lastMessageSentAt": "2025-07-14T04:22:39.505848Z"
                                                    },
                                                    {
                                                        "id": "dc464245-f51a-4920-a841-f45cda0435b2",
                                                        "type": "PUBLIC",
                                                        "name": "❓학습QnA📖",
                                                        "description": "❓학습QnA📖 채널의 시작이에요. 학습 질의응답을 위한 채널이에요.  학습 내용에 관한 질문이 있다면, 이 채널에 올려주세요.",
                                                        "participantIds": [],
                                                        "lastMessageSentAt": "2025-07-20T15:31:21.796663Z"
                                                    },
                                                    {
                                                        "id": "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63",
                                                        "type": "PUBLIC",
                                                        "name": "📗일반-공지",
                                                        "description": "일반 공지입니다.",
                                                        "participantIds": [],
                                                        "lastMessageSentAt": "2025-07-14T04:27:42.240070Z"
                                                    },
                                                    {
                                                        "id": "395b0bd9-a145-499f-ae6b-6bbacbf65651",
                                                        "type": "PRIVATE",
                                                        "name": null,
                                                        "description": null,
                                                        "participantIds": [
                                                            "b2679878-5a10-41a3-b4b0-008ae83da8dd",
                                                            "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                            "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4"
                                                        ],
                                                        "lastMessageSentAt": "2025-07-20T15:21:53.078720Z"
                                                    },
                                                    {
                                                        "id": "3ce796f4-0c1b-431d-b7be-43ba9103c028",
                                                        "type": "PRIVATE",
                                                        "name": null,
                                                        "description": null,
                                                        "participantIds": [
                                                            "b2679878-5a10-41a3-b4b0-008ae83da8dd",
                                                            "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                            "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4",
                                                            "992fe09c-238b-47ee-a5a1-b38ad7a7ac95"
                                                        ],
                                                        "lastMessageSentAt": "2025-07-20T07:11:34.388005Z"
                                                    },
                                                    {
                                                        "id": "21607160-3825-4a6d-a18c-72993ceb4812",
                                                        "type": "PRIVATE",
                                                        "name": null,
                                                        "description": null,
                                                        "participantIds": [
                                                            "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                            "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4"
                                                        ],
                                                        "lastMessageSentAt": "2025-07-20T14:22:42.341197Z"
                                                    }
                                                ]
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 사용자(userId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "채널이  존재하지 않을 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "Channel with id cc8f67a7-55c8-46f8-a909-b323f14136c4 not found",
                                                    "timestamp": "2025-07-09T17:06:20.219015"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<List<ChannelResponseDto>> getChannels(UUID userId);

    @Operation(summary = "채널 부분 수정", description = "채널 아이디(channelId)에 해당하는 채널을 부분적으로 수정합니다.",
            parameters = @Parameter(
                    name        = "channelId",
                    in          = ParameterIn.PATH,
                    description = "채널 ID (UUID)",
                    required    = true,
                    example     = "270d615f-8b0d-41a8-a3bc-1e008d6df0e2"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 수정되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "성공 예시",
                                    value = """
                                                {
                                                    "id": "270d615f-8b0d-41a8-a3bc-1e008d6df0e2",
                                                    "type": "PUBLIC",
                                                    "name": "📒학습-공지",
                                                    "description": "#📒학습-공지 채널의 시작이에요. 학습 관련 사항이 공지되는 채널이에요. 이 채널에 공유되는 소식은 모든 멤버가 꼭 확인해 주세요!",
                                                    "participantIds": [],
                                                    "lastMessageSentAt": "2025-07-14T04:22:39.505848Z"
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 채널(channelId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "채널이  존재하지 않을 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "Channel with id cc8f67a7-55c8-46f8-a909-b323f14136c4 not found",
                                                    "timestamp": "2025-07-09T17:06:20.219015"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<ChannelResponseDto> updateChannel(UUID channelId, ChannelUpdateDto dto);

    @Operation(summary = "채널 삭제", description = "채널 아이디(channelId)로 채널을 삭제합니다.",
            parameters = @Parameter(
                    name        = "channelId",
                    in          = ParameterIn.PATH,
                    description = "채널 ID (UUID)",
                    required    = true,
                    example     = "bfe59536-7c85-435d-9b49-d3b7fe08096d"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "정상적으로 삭제되었습니다. (이미 삭제된 경우 404가 뜰 수 있습니다)",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "성공적으로 삭제된 경우 Body에 데이터 없이 204 응답코드만 전달됩니다.",
                                    value = ""
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 채널(channelId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "채널이  존재하지 않을 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "Channel with id cc8f67a7-55c8-46f8-a909-b323f14136c4 not found",
                                                    "timestamp": "2025-07-09T17:06:20.219015"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<Void> deleteChannel(UUID channelId);
}
