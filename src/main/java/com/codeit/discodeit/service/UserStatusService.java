package com.codeit.discodeit.service;

import com.codeit.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public interface UserStatusService {

  UserStatus createUserStatus(UUID userId);

  UserStatus updateUserStatus(UUID userId, Instant newLastAt);

  UserStatus findUserStatusByUserId(UUID userId);
}
