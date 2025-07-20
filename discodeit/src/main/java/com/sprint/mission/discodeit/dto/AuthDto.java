package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusResponse;

@Schema(description = "로그인")
public class AuthDto {

    @Schema(description = "로그인 정보")
    public record LoginRequest(
            @Schema(description = "유저 이름", example = "codeit1234", format = "string")
            String username,
            @Schema(description = "password", example = "q1w2e3r4", format = "string")
            String password
    ) {

    }

    @Schema(description = "로그인 응답")
    public record LoginResponse(
            @Schema(description = "userId", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46", format = "uuid")
            UUID userId,
            @Schema(description = "User 이름", example = "codeit1234")
            String username,
            @Schema(description = "email", example = "codeit@gmail.com")
            String email,
            @Schema(description = "유저 상태")
            UserStatusResponse userStatusResponse
    ) {

    }
}
