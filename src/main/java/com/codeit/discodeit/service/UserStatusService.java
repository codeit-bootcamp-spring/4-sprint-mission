package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public interface UserStatusService {

  UserStatus createUserStatus(User user);
  UserStatusDto updateUserStatus(UUID userId, Instant newLastAt);
}
