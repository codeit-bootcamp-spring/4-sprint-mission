package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;


  @Override
  public UserStatus createUserStatus(UUID userId) {

    User user = findUserByUserId(userId);

    Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(user.getId());

    if (userStatus.isPresent()) {
      return userStatus.get();
    }

    UserStatus newUserStatus = new UserStatus(user); // 회원 가입할 때는 false로 생성
    userStatusRepository.createUserStatus(newUserStatus);
    return newUserStatus;
  }

  @Override
  public UserStatus updateUserStatus(UUID userId, Instant newLastAt) {
    Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(userId);
    
    if (userStatus.isEmpty()) {
      return createUserStatus(userId);
    }

    UserStatus updateUserStatus = userStatus.get();

    updateUserStatus.setLastActiveAt(newLastAt); // 이게 정답입니다.
    userStatusRepository.updateUserStatus(updateUserStatus);

    return updateUserStatus;
  }

  @Override
  public UserStatus findUserStatusByUserId(UUID userId) {
    Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserStatusId(userId);
    User user = findUserByUserId(userId);

    if (userStatus.isEmpty()) {
      return createUserStatus(userId);
    }

    return userStatus.get();
  }

  private User findUserByUserId(UUID userId) {
    return userRepository.findUserById(userId)
        .orElseThrow(() -> new IllegalStateException("해당하는 유저를 찾을 수 없습니다."));
  }
}
