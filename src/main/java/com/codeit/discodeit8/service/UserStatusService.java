package com.codeit.discodeit8.service;

import com.codeit.discodeit8.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public interface UserStatusService {

  UserStatus createUserStatus(User user);

  UserStatusDto updateUserStatus(UUID userId, Instant newLastAt);
}
