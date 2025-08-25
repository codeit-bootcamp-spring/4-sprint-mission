package com.codeit.discodeit.service.basic;

import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.entity.*;
import com.codeit.discodeit.exception.user.UserStatusNotFoundException;
import com.codeit.discodeit.mapper.UserStatusMapper;
import com.codeit.discodeit.repository.UserStatusRepository;
import com.codeit.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;

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
  public UserStatusDto updateUserStatus(UUID userId, Instant newLastAt) {
    Optional<UserStatus> userStatus = userStatusRepository.findByUserId(userId);
    
    if (userStatus.isEmpty()) {
      Map<String, Object> details = Map.of(
          "이유", "유저 상태 없음"
      );
      throw new UserStatusNotFoundException(details);
    }

    UserStatus updateUserStatus = userStatus.get();

    updateUserStatus.setLastActiveAt(newLastAt);
    userStatusRepository.save(updateUserStatus);

    return userStatusMapper.toUserStatusDto(updateUserStatus);
  }
}
