package com.codeit.discodeit8.service;

import com.codeit.discodeit8.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit8.dto.user_service_dto.UserDto;

public interface AuthService {

  UserDto logInUser(LoginRequestDto loginRequestDTO);
}
