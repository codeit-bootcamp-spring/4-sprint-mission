package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.ErrorCode;
import com.codeit.discodeit.exception.exception.BusinessException;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserStatus createUserStatus(User user) {
    UserStatus newUserStatus = new UserStatus();
    newUserStatus.setUser(user);
    //userStatusRepository.createUserStatus(newUserStatus); // user 연결되어서 대신 생성

    return newUserStatus;
  }

  @Override
  @Transactional
  public UserStatus updateUserStatus(UUID userId, Instant newLastAt) {
    Optional<UserStatus> userStatus = userStatusRepository.findUserStatusByUserId(userId);
    
    if (userStatus.isEmpty()) {
      throw new BusinessException(ErrorCode.NO_FIND_USER_STATUS);
    }

    UserStatus updateUserStatus = userStatus.get();

    updateUserStatus.setLastActiveAt(newLastAt);
    userStatusRepository.updateUserStatus(updateUserStatus);

    return updateUserStatus;
  }
}
