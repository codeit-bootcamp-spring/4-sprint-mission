package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "💬 Message", description = "메시지 관련 API")
public interface MessageApi {

    @Operation(
            summary = "메시지 생성",
            description = "JSON DTO + 이미지(바이너리)를 동시에 업로드하여 메시지를 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MessageCreateDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정상적으로 생성되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "성공 예시",
                                    value = """
                                                {
                                                    "id": "d67a8775-7a3a-49ec-b2d4-018e15db0683",
                                                    "channelId": "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63",
                                                    "authorId": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                    "content": "필독입니다",
                                                    "attachmentIds": [
                                                        "3061bca4-b6d6-42d2-93c9-1946c00d18f6"
                                                    ],
                                                    "createdAt": "2025-07-20T15:35:49.152194Z",
                                                    "updatedAt": "2025-07-20T15:35:49.152195Z"
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name    = "BadRequest Example (INVALID)",
                                            summary = "필드 유효성 검사에 실패한 경우",
                                            value   = """
                                                        {
                                                          "success": false,
                                                          "code": 400,
                                                          "message": "유효성 검사 실패",
                                                          "data": [
                                                              {
                                                                  "field": "authorId",
                                                                  "message": "널이어서는 안됩니다"
                                                              },
                                                              {
                                                                  "field": "content",
                                                                  "message": "공백일 수 없습니다"
                                                              },
                                                              {
                                                                  "field": "channelId",
                                                                  "message": "널이어서는 안됩니다"
                                                              }
                                                          ],
                                                          "timestamp": "2025-07-09T18:10:15.90603"
                                                      }
                                                      """
                                    )
                            }
                    )),
            @ApiResponse(responseCode = "415", description = "파일 입출력 중 오류가 발생했습니다.",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "FileIOException Example",
                                    summary = "Unsupported Media Type” 또는 서버 I/O 오류",
                                    value = """
                                        {
                                            "success": false,
                                            "code": 415,
                                            "message": "파일 입출력 중 오류가 발생했습니다.",
                                            "data": null,
                                            "timestamp": "2025-07-09T15:42:13.725376"
                                        }
                                    """
                            )
                    ))
    })
    ResponseEntity<MessageResponseDto> createMessage(MessageCreateDto dto, List<MultipartFile> attachments);


    @Operation(summary = "특정 채널의 메시지 전체 조회", description = "특정 채널(channelId)의 메시지 목록을 전체 조회합니다.",
            parameters = @Parameter(
                    name        = "channelId",
                    in          = ParameterIn.QUERY,
                    description = "채널 ID (UUID)",
                    required    = true,
                    example     = "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 조회되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "성공 예시",
                                    value = """
                                                [
                                                     {
                                                         "id": "af4a635a-eab1-43ea-a821-3b3c5fa81984",
                                                         "channelId": "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63",
                                                         "authorId": "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4",
                                                         "content": "출석 인정 관련 안내\\n출석 인정 관련 중요한 내용 안내드립니다.",
                                                         "attachmentIds": null,
                                                         "createdAt": "2025-07-20T06:50:53.767785Z",
                                                         "updatedAt": "2025-07-20T06:50:53.767785Z"
                                                     },
                                                     {
                                                         "id": "d67a8775-7a3a-49ec-b2d4-018e15db0683",
                                                         "channelId": "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63",
                                                         "authorId": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                         "content": "필독입니다",
                                                         "attachmentIds": [
                                                             "3061bca4-b6d6-42d2-93c9-1946c00d18f6"
                                                         ],
                                                         "createdAt": "2025-07-20T15:35:49.152194Z",
                                                         "updatedAt": "2025-07-20T15:35:49.152195Z"
                                                     }
                                                 ]
                                            """
                            )
                    ))
    })
    ResponseEntity<List<MessageResponseDto>> getMessages(UUID channelId);


    @Operation(
            summary = "메시지 부분 수정",
            description = "JSON DTO + 이미지(바이너리)를 업로드하여 특정 메시지(messageId)를 부분적으로 수정합니다.",
            parameters = @Parameter(
                    name = "messageId",
                    in = ParameterIn.PATH,
                    description = "메시지 ID",
                    required = true,
                    example = "af4a635a-eab1-43ea-a821-3b3c5fa81984"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MessageUpdateDto.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 수정되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "SuccessExample",
                                    summary = "성공 예시",
                                    value = """
                                                {
                                                    "id": "d67a8775-7a3a-49ec-b2d4-018e15db0683",
                                                    "channelId": "a5a0f4f5-ca67-4d3d-ab62-d4907b21ec63",
                                                    "authorId": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                    "content": "필독입니다2",
                                                    "attachmentIds": [
                                                        "3061bca4-b6d6-42d2-93c9-1946c00d18f6"
                                                    ],
                                                    "createdAt": "2025-07-20T15:35:49.152194Z",
                                                    "updatedAt": "2025-07-20T15:40:17.041192Z"
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name    = "BadRequest Example (INVALID)",
                                    summary = "필드 유효성 검사에 실패한 경우",
                                    value   = """
                                                        {
                                                          "success": false,
                                                          "code": 400,
                                                          "message": "유효성 검사 실패",
                                                          "data": [
                                                              {
                                                                  "field": "id",
                                                                  "message": "널이어서는 안됩니다"
                                                              },
                                                              {
                                                                  "field": "content",
                                                                  "message": "공백일 수 없습니다"
                                                              }
                                                          ],
                                                          "timestamp": "2025-07-09T18:16:26.728659"
                                                        }
                                                      """
                            )

                    )),
            @ApiResponse(responseCode = "404", description = "해당 메시지(messageId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "메시지가 존재하지 않을 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "Message with id d77d7333-cc37-4e34-9c3f-849c2c058a77 not found",
                                                    "timestamp": "2025-07-09T17:39:15.977931"
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "415", description = "파일 입출력 중 오류가 발생했습니다.",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "FileIOException Example",
                                    summary = "Unsupported Media Type” 또는 서버 I/O 오류",
                                    value = """
                                        {
                                            "success": false,
                                            "code": 415,
                                            "message": "파일 입출력 중 오류가 발생했습니다.",
                                            "data": null,
                                            "timestamp": "2025-07-09T15:42:13.725376"
                                        }
                                    """
                            )
                    ))
    })
    ResponseEntity<MessageResponseDto> updateMessage(UUID messageId, MessageUpdateDto dto);


    @Operation(summary = "메시지 삭제", description = "메시지 아이디(messageId)로 메시지를 삭제합니다.",
            parameters = @Parameter(
                    name = "messageId",
                    in = ParameterIn.PATH,
                    description = "메시지 ID",
                    required = true,
                    example = "1c0d1342-6542-4afd-9c41-7ef68c353321"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "정상적으로 삭제되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "성공적으로 삭제된 경우 Body에 데이터 없이 204 응답코드만 전달됩니다.",
                                    value = ""
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 메시지(messageId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "메시지가 존재하지 않을 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "Message with id d77d7333-cc37-4e34-9c3f-849c2c058a77 not found",
                                                    "timestamp": "2025-07-09T17:39:15.977931"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<MessageResponseDto> deleteMessage(UUID messageId);
}
