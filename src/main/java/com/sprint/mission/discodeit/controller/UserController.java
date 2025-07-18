package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.OptionalBinaryContentMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final OptionalBinaryContentMapper binaryContentMapper;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 생성", description = "신규 유저 정보를 생성합니다. 프로필 이미지는 선택적으로 업로드할 수 있습니다")
    @ApiResponse(responseCode = "201", description = "사용자 생성 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "415", description = "잘못된 타입의 데이터입니다")
    public ResponseEntity<UserDto> postUser(

            @Parameter(
                    description = "유저 정보 json",
                    required = true,
                    schema =@Schema(type = "string", format = "binary"))//스웨거 테스트용. json을 파일로 넣어줘야 한다.
            @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
            @Parameter(
                    description = "User 프로필 이미지",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        Optional<BinaryContentCreateRequest> optionalProfile = binaryContentMapper.toBinaryContentCreateRequest(profile);

        User user = userService.create(userCreateRequest, optionalProfile);
        UserDto userDto = userService.makeDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 정보 수정", description = "유저의 일부 정보를 수정합니다. 프로필 이미지는 선택적으로 업로드할 수 있습니다")
    @ApiResponse(responseCode = "200", description = "유저 생성 완료")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다")
    public ResponseEntity<UserDto> patchUser(
            @Parameter(description = "수정할 유저의 UUID")
            @PathVariable("userId") UUID userId,
//            @Parameter(
//                    description = "유저 수정 요청 json",
//                    required = true,
//                    content = @Content(
//                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                            schema = @Schema(implementation = UserUpdateRequest.class)
//                    )
//            )
            @Parameter(
                    description = "유저 수정 요청 json",
                    required = true,
                    schema =@Schema(type = "string", format = "binary"))
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile) {

        Optional<BinaryContentCreateRequest> optionalProfile = binaryContentMapper.toBinaryContentCreateRequest(profile);
        User editedUser = userService.update(userId, userUpdateRequest, optionalProfile);
        UserDto userDto = userService.makeDto(editedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);

    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @Operation(summary = "유저 삭제", description = "등록된 유저를 삭제합니다")
    @ApiResponse(responseCode = "204", description = "유저 삭제 완료")
    @ApiResponse(responseCode = "400", description = "잘못된 UUID 형식 입니다")
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 유저의 UUID")
            @PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "모든 유저 조회", description = "등록된 모든 유저를 조회합니다")
    @ApiResponse(responseCode = "200", description = "유저 조회 성공")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = userService.findAll();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @Operation(summary = "유저 접속 정보 수정", description = "해당 유저의 접속 정보를 수정합니다")
    @ApiResponse(responseCode = "400", description = "JSON 형식이 잘못되었거나 필수 파라미터가 누락되었습니다")
    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다")
    @ApiResponse(responseCode = "200", description = "접속 정보 수정 성공")
    @RequestMapping(value = "/{userId}/userStatus", method = RequestMethod.PATCH)
    public ResponseEntity<UserDto> patchUserStatus(
            @Parameter(description = "접속 정보를 수정할 유저의 UUID")
            @PathVariable("userId") UUID userId,
            @RequestBody UserStatusUpdateRequest request) {
        userStatusService.updateByUserId(userId, request);
        UserDto userDto = userService.find(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
