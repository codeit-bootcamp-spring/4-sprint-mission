package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
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

@Tag(name = "🙂 User", description = "사용자 관련 API")
public interface UserApi {
    @Operation(
            summary = "사용자 생성",
            description = "JSON DTO + 프로필 이미지(바이너리)를 동시에 업로드하여 사용자를 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UserCreateDto.class)
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
                                                    "id": "778ab03e-0f22-403c-b788-fae4c174eae7",
                                                    "username": "anxiety",
                                                    "email": "anxiety@inside.out",
                                                    "profileId": "839493de-3f94-4b06-bc10-6cad1eed8f11",
                                                    "createdAt": "2025-07-20T15:49:09.973878Z",
                                                    "updatedAt": "2025-07-20T15:49:09.973878Z",
                                                    "online": true
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다 (중복 사용자, 유효성 실패 등)",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name    = "BadRequest Example",
                                            summary = "이미 존재하는 사용자명인 경우 (DUPLICATE)",
                                            value   = """
                                                        {
                                                          "success": false,
                                                          "code": 400,
                                                          "message": "Bad Request Exception",
                                                          "data": "This username maru is already in use",
                                                          "timestamp": "2025-07-09T15:42:13.725376"
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
                    )),
    })
    ResponseEntity<UserResponseDto> create(UserCreateDto dto, MultipartFile profile);

    @Operation(summary = "전체 사용자 조회", description = "전체 사용자를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 조회되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "성공 예시",
                                    value = """
                                                [
                                                    {
                                                        "id": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                        "username": "joy123",
                                                        "email": "joy123@inside.out",
                                                        "profileId": "e2189e67-cf5e-46b9-be45-d8299a1fc298",
                                                        "createdAt": "2025-07-14T05:25:07.135099Z",
                                                        "updatedAt": "2025-07-20T14:30:23.159294Z",
                                                        "online": true
                                                    },
                                                    {
                                                        "id": "778ab03e-0f22-403c-b788-fae4c174eae7",
                                                        "username": "anxiety",
                                                        "email": "anxiety@inside.out",
                                                        "profileId": "839493de-3f94-4b06-bc10-6cad1eed8f11",
                                                        "createdAt": "2025-07-20T15:49:09.973878Z",
                                                        "updatedAt": "2025-07-20T15:49:09.973878Z",
                                                        "online": true
                                                    },
                                                    {
                                                        "id": "b2679878-5a10-41a3-b4b0-008ae83da8dd",
                                                        "username": "gosimshoi",
                                                        "email": "gosim@email.com",
                                                        "profileId": "7cbe0d73-b1b6-4aca-9006-6c047c2c8b68",
                                                        "createdAt": "2025-07-14T04:36:28.804278Z",
                                                        "updatedAt": "2025-07-14T04:36:28.804278Z",
                                                        "online": false
                                                    },
                                                    {
                                                        "id": "64fbc1f4-c9e4-4178-85c9-2ed2aa434ce4",
                                                        "username": "maru",
                                                        "email": "maru132@gmail.com",
                                                        "profileId": "5d2ec995-8953-4cb4-a263-5471299c393d",
                                                        "createdAt": "2025-07-14T04:06:32.212491Z",
                                                        "updatedAt": "2025-07-18T07:41:11.766501Z",
                                                        "online": false
                                                    },
                                                    {
                                                        "id": "992fe09c-238b-47ee-a5a1-b38ad7a7ac95",
                                                        "username": "saddy",
                                                        "email": "sad@inside.out",
                                                        "profileId": "577ceb8a-51f3-4bf6-b66c-9d5c6a9b319d",
                                                        "createdAt": "2025-07-20T07:11:12.487842Z",
                                                        "updatedAt": "2025-07-20T07:11:12.487842Z",
                                                        "online": false
                                                    }
                                                ]
                                            """
                            )
                    ))
    })
    ResponseEntity<List<UserResponseDto>> getUsers();


    @Operation(
            summary = "사용자 부분 수정",
            description = "JSON DTO + 프로필 이미지(바이너리)를 업로드하여 특정 사용자(userId)를 부분적으로 수정합니다.",
            parameters = @Parameter(
                    name = "userId",
                    in = ParameterIn.PATH,
                    description = "사용자 ID",
                    required = true,
                    example  = "82170d94-e9e6-45df-874a-f4fb949f0835"
            ),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UserUpdateDto.class)
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
                                                    "id": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                    "username": "joy1234",
                                                    "email": "joy1234@inside.out",
                                                    "profileId": "e2189e67-cf5e-46b9-be45-d8299a1fc298",
                                                    "createdAt": "2025-07-14T05:25:07.135099Z",
                                                    "updatedAt": "2025-07-20T15:56:16.735601Z",
                                                    "online": true
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 사용자(userId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "사용자가 존재하지 않을 경우 Not Found",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "User with id 61604874-42a0-408b-a3a1-42eb300fbf8f not found",
                                                    "timestamp": "2025-07-09T16:22:48.540675"
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
    ResponseEntity<UserResponseDto> updateUser(UUID userId, UserUpdateDto dto, MultipartFile profile);


    @Operation(summary = "사용자의 접속 시간 업데이트", description = "특정 사용자(userId)의 마지막으로 확인된 접속 시간을 업데이트합니다.",
            parameters = @Parameter(
                    name = "userId",
                    in = ParameterIn.PATH,
                    description = "사용자 ID (UUID)",
                    required = true,
                    example = "82170d94-e9e6-45df-874a-f4fb949f0835"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "접속 시간이 정상적으로 갱신되었습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "성공적으로 업데이트된 예시",
                                    value = """
                                                {
                                                    "id": "82170d94-e9e6-45df-874a-f4fb949f0835",
                                                    "username": "joy123",
                                                    "email": "joy123@inside.out",
                                                    "profileId": "e2189e67-cf5e-46b9-be45-d8299a1fc298",
                                                    "createdAt": "2025-07-14T05:25:07.135099Z",
                                                    "updatedAt": "2025-07-20T14:30:23.159294Z",
                                                    "online": true
                                                }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "해당 사용자(userId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "사용자가 존재하지 않을 경우 Not Found",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "User with id 61604874-42a0-408b-a3a1-42eb300fbf8f not found",
                                                    "timestamp": "2025-07-09T16:22:48.540675"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<UserResponseDto> updateUserStatusByUserId(UUID userId);


    @Operation(summary = "특정 사용자 삭제", description = "특장 사용자(userId)를 삭제합니다",
            parameters = @Parameter(
                    name = "userId",
                    in = ParameterIn.PATH,
                    description = "사용자 ID (UUID)",
                    required = true,
                    example = "e5fb994a-0ec7-442f-8214-92919c5fedd6"
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
            @ApiResponse(responseCode = "404", description = "해당 사용자(userId)가 존재하지 않습니다",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "NotFound Example",
                                    summary = "사용자가 존재하지 않을 경우 예시",
                                    value = """
                                                {
                                                    "success": false,
                                                    "code": 404,
                                                    "message": "Not Found Exception",
                                                    "data": "User with id 61604874-42a0-408b-a3a1-42eb300fbf8f not found",
                                                    "timestamp": "2025-07-09T16:22:48.540675"
                                                }
                                            """
                            )
                    ))
    })
    ResponseEntity<Void> deleteUser(UUID userId);


}
