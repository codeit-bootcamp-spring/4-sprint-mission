package com.codeit.discodeit.dto.auth_service_dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {

  private String username;
  private String password;

  public LoginRequestDto() {
    // 기본 생성자 (반드시 있어야 함)
  }

  public LoginRequestDto(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
