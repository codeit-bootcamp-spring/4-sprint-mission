package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusResponse;

@Schema(description = "사용자 DTO")
public class UserDto {

    //유저 등록
    @Schema(description = "사용자 등록 요청")
    public record UserCreateRequest(
            @Schema(description = "사용자 이름", example = "jin", requiredMode = Schema.RequiredMode.REQUIRED)
            String username,

            @Schema(description = "이메일 주소", example = "jin@nate.com", requiredMode = Schema.RequiredMode.REQUIRED)
            String email,

            @Schema(description = "비밀번호", example = "jin1234!", requiredMode = Schema.RequiredMode.REQUIRED)
            String password
    ) {

    }

    @Schema(description = "사용자 정보 응답")
    public record UserResponse(
            @Schema(description = "사용자 고유 ID", example = "63027b9a-ee72-4537-bb48-7251a78ca9fa")
            UUID id,

            @Schema(description = "사용자 이름", example = "jin")
            String username,

            @Schema(description = "이메일 주소", example = "jin@nate.com")
            String email,

            @Schema(description = "프로필 사진 ID", example = "98ecadbb-9ebe-45ff-9f24-6b20545e32af")
            UUID profileId,

            @Schema(description = "사용자 현재 상태 정보")
            UserStatusResponse userStatusResponse
    ) {

    }


    @Schema(name = "User", description = "생성된 사용자 정보 상세 응답")
    public record UserResponseDto(
            @Schema(description = "사용자 고유 ID", example = "3af067fa-309b-4f84-a10d-8aefc579a191")
            UUID id,

            @Schema(description = "생성 시각", example = "2025-07-20T18:45:21.055825")
            Instant createdAt,

            @Schema(description = "마지막 수정 시각", example = "2025-07-20T18:45:21.055825")
            Instant updatedAt,

            @Schema(description = "사용자 이름", example = "suijjang")
            String username,

            @Schema(description = "이메일 주소", example = "sui@nate.com")
            String email,

            @Schema(description = "비밀번호", example = "sui1234!")
            String password,

            @Schema(description = "프로필 사진 ID", example = "82b45ceb-91c7-49b3-b6e1-633c14fd0e21")
            UUID profileId
    ) {

    }

    //유저 조회
    @Schema(description = "전체 사용자 목록의 단일 사용자 정보")
    public record AllUserResponseDto(
            @Schema(description = "사용자 고유 ID", example = "3af067fa-309b-4f84-a10d-8aefc579a191")
            UUID id,

            @Schema(description = "생성 시각", example = "2025-07-20T18:45:21.055825")
            Instant createdAt,

            @Schema(description = "마지막 수정 시각", example = "2025-07-20T18:45:21.055825")
            Instant updatedAt,

            @Schema(description = "사용자 이름", example = "suijjang")
            String username,

            @Schema(description = "이메일 주소", example = "sui@nate.com")
            String email,

            @Schema(description = "프로필 사진 ID", example = "82b45ceb-91c7-49b3-b6e1-633c14fd0e21")
            UUID profileId,

            @Schema(description = "온라인 상태 여부", example = "true")
            Boolean online
    ) {

    }


    //유저 수정
    @Schema(description = "수정할 사용자 정보")
    public record UserUpdateRequest(
            @Schema(description = "새로운 사용자 이름", example = "viichan")
            String newUsername,

            @Schema(description = "새로운 비밀번호", example = "viichan1234!")
            String newPassword,

            @Schema(description = "새로운 이메일 주소", example = "viichan@nate.com")
            String newEmail
    ) {

    }


    @Schema(description = "사용자 정보 수정 응답")
    public record UserUpdateResponse(
            @Schema(description = "수정된 사용자 이름", example = "viichan")
            String username,

            @Schema(description = "수정된 이메일 주소", example = "viichan@nate.com")
            String email,

            @Schema(description = "프로필 사진 ID", example = "82b45ceb-91c7-49b3-b6e1-633c14fd0e21")
            UUID profileId,

            @Schema(description = "사용자 고유 ID", example = "3af067fa-309b-4f84-a10d-8aefc579a191")
            UUID id,

            @Schema(description = "생성 시각", example = "2025-07-20T18:45:21.055825")
            Instant createdAt,

            @Schema(description = "마지막 수정 시각", example = "2025-07-20T19:04:51.964227")
            Instant updatedAt,

            @Schema(description = "비밀번호", example = "viichan1234")
            String password
    ) {

    }
}
