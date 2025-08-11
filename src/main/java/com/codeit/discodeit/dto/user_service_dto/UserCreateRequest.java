package com.codeit.discodeit.dto.user_service_dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "사용자 이름", example = "yj_k")
  private String username;

  @Schema(description = "비밀번호", example = "1234abcd!")
  private String password;

  @Schema(description = "이메일", example = "yj@example.com")
  private String email;

  @Schema(description = "프로필 이미지")
  private MultipartFile profileImage;
}
