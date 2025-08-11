package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.entity.User;

public interface AuthService {

  User logInUser(LoginRequestDto loginRequestDTO);
}
