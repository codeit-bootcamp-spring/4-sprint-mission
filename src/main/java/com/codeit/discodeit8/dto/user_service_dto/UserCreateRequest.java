package com.codeit.discodeit8.dto.user_service_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "User 생성 정보")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

  @NotBlank
  @Size(min = 4, max = 14)
  @Schema(description = "사용자 이름", example = "yj_k")
  private String username;

  @NotBlank
  @Size(min = 4, max = 14)
  @Schema(description = "비밀번호", example = "1234abcd!")
  private String password;

  @NotBlank
  @Size(min = 10, max = 30)
  @Email
  @Schema(description = "이메일", example = "yj@example.com")
  private String email;

  @Schema(description = "프로필 이미지")
  private MultipartFile profileImage;
}
