package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusDto.create request);

    UserStatus find(UUID userStatusId);

    UserStatus findByUserId(UUID userId);

    List<UserStatus> findAll();

    UserStatus update(UUID userStatusId, UserStatusDto.update request);

    UserStatus updateByUserId(UUID userId, UserStatusDto.update request);

    void delete(UUID userStatusId);
}
