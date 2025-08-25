package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;

public interface AuthService {

  UserDto logInUser(LoginRequestDto loginRequestDTO);
}
