package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public class AuthDto {

  @Schema(description = "로그인 정보")
  public record LoginRequest(
      @Schema(description = "The username of the user", example = "codeit1234", format = "string")
      String username,
      @Schema(description = "The password of the user", example = "q1w2e3r4", format = "string")
      String password
  ) {

  }

  public record LoginResponse(
      @Schema(description = "The UUID of the user", example = "c1244d8c-77db-4c61-823f-0a83cc91bb46", format = "uuid")
      UUID userId,
      @Schema(description = "The username of the user", example = "codeit1234")
      String username,
      @Schema(description = "The email of the user", example = "codeit@gmail.com")
      String email,
      @Schema(description = "The UserStatus of the user")
      UserStatusResponse userStatusResponse
  ) {

  }
}
