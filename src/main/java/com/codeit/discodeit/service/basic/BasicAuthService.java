package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.exception.user.UserNotFoundException;
import com.codeit.discodeit.exception.user.UserWrongPasswordException;
import com.codeit.discodeit.mapper.UserMapper;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto logInUser(LoginRequestDto loginRequest) {

    User user = findUserByUserName(loginRequest.getUsername());
    String rawPassword = loginRequest.getPassword();

    if (!rawPassword.equals(user.getPassword())) {
      Map<String, Object> details = Map.of(
          "이유", "비밀번호 불일치"
      );
      throw new UserWrongPasswordException(details);
    }
    return userMapper.toUserDto(user);
  }

  private User findUserByUserName(String userName) {
    return userRepository.findUserByUsername(userName)
        .orElseThrow(() -> {
          Map<String, Object> details = Map.of(
              "이유", "없음"
          );
          return new UserNotFoundException(details);
        });
  }

}
