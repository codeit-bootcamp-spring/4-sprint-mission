package com.codeit.discodeit.dto.user_service_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "수정할 User 정보")
public class UserUpdateRequest {

  @Schema(description = "수정할 사용자 ID")
  private UUID userId;

  @NotBlank
  @Size(min = 4, max = 14)
  @Schema(description = "새 사용자 이름", example = "newUsername123")
  private String newUsername;

  @NotBlank
  @Size(min = 10, max = 30)
  @Email
  @Schema(description = "새 이메일 주소", example = "newEmail@example.com")
  private String newEmail;

  @NotBlank
  @Size(min = 4, max = 14)
  @Schema(description = "새 비밀번호", example = "newSecurePassword123!")
  private String newPassword;
}