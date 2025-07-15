package com.codeit.discodeit.dto.user_service_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "User 생성 정보")
@Setter
@Getter
public class UserCreateRequest {

  @Schema(description = "사용자 이름", example = "yj_k")
  private String username;

  @Schema(description = "비밀번호", example = "1234abcd!")
  private String password;

  @Schema(description = "이메일", example = "yj@example.com")
  private String email;

  @Schema(description = "프로필 이미지")
  private MultipartFile profileImage;

  // 생성자, getter/setter, builder 등 추가
  public UserCreateRequest(String username, String password, String email,
      MultipartFile profileImage) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.profileImage = profileImage;
  }

  public UserCreateRequest() {
  }
}
