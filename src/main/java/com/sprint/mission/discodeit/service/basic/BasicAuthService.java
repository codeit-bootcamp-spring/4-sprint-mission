package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final AuthMapper authMapper;
  private final UserMapper userMapper;

  @Override
  public UserDto.UserGetDto login(AuthDto.LoginRequest loginRequest) {

    // checking if username exists
    User user = userRepository.findByUsername(loginRequest.username())
        .orElseThrow(() -> new NoSuchElementException("Invalid username - username not found!"));

    // checking if pw equals to the pw in the repo
    if (!user.getPassword().equals(loginRequest.password())) {
      throw new IllegalArgumentException("Invalid password!");
    }

    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(() -> new NoSuchElementException(
            "User Status with user id " + user.getId() + " not found!"));

    // update userStatus and save
    userStatus.updateLastActiveTime();
    userStatusRepository.save(userStatus);

//        return authMapper.toLoginResponseDto(user, userStatus);
    return userMapper.toUserGetDto(user, userStatus);
  }
}
