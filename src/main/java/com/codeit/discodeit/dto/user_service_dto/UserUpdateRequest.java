package com.codeit.discodeit.dto.user_service_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "수정할 User 정보")
public class UserUpdateRequest {

  @Schema(description = "수정할 사용자 ID")
  private UUID userId;

  @Schema(description = "새 사용자 이름", example = "newUsername123")
  private String newUsername;

  @Schema(description = "새 이메일 주소", example = "new.email@example.com")
  private String newEmail;

  @Schema(description = "새 비밀번호", example = "newSecurePassword123!")
  private String newPassword;

  public UserUpdateRequest(String newUsername, String newEmail, String newPassword) {
    this.newUsername = newUsername;
    this.newEmail = newEmail;
    this.newPassword = newPassword;
  }
}
