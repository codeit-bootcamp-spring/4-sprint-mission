package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public interface UserStatusService {
    UserStatus create(User user);

    UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest);

    void delete(UUID id);
}
