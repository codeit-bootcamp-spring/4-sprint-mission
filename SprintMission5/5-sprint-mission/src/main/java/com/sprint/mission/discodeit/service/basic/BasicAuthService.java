package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.login.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.WrongPasswordException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto login(LoginRequest loginRequest) {
    UserEntity userEntity = authenticate(loginRequest);
    updateLastAccessTime(userEntity.getId());
    return userMapper.toUserDto(userEntity);
  }

  private UserEntity authenticate(LoginRequest loginRequest) {
    UserEntity userEntity = userRepository.findByUsername(loginRequest.username())
        .orElseThrow(() -> new UserNotFoundException(
            "User with username " + loginRequest.username() + " not found"));

    if (!userEntity.getPassword().equals(loginRequest.password())) {
      throw new WrongPasswordException("Wrong password");
    }
    return userEntity;
  }

  private void updateLastAccessTime(UUID userId) {
    userStatusRepository.findByUserId(userId)
        .ifPresent(userStatus -> {
          userStatus.updateAccessTime();
          userStatusRepository.save(userStatus);
        });
  }


}
