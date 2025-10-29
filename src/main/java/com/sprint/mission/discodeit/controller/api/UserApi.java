package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(summary = "User 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함"
            )
    })
    ResponseEntity<UserResponse> createUser(
            @Parameter(
                    description = "User 생성 요청",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) UserCreateRequest userCreateRequest,
            @Parameter(
                    description = "프로필 이미지",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) MultipartFile profile);


    @Operation(summary = "User 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함"
            )
    })
    ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "수정할 User Id") UUID userId,
            @Parameter(
                    description = "수정할 User 정보",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) UserUpdateRequest userCreateRequest,
            @Parameter(
                    description = "수정할 User 프로필 이미지",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ) MultipartFile profile);


    @Operation(summary = "User 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제됨",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음"
            )
    })
    ResponseEntity<Void> deleteUser(@Parameter(description = "삭제할 User Id") UUID userId);


    @Operation(summary = "User 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    ResponseEntity<UserResponse> findUser(@Parameter(description = "조회할 User Id") UUID userId);

    @Operation(summary = "전체 User 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))
                    ))
    })
    ResponseEntity<List<UserResponse>> findUsers();
}
