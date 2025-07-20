package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponse create(UserStatusRequest statusRequest);
    UserStatusResponse findById(UUID id);
    List<UserStatusResponse> findAll();
    UserStatusUpdateResponse update(UUID userId, UserStatusUpdateRequest updateRequest);
    UserStatusResponse updateByUserId(UUID userId);
    void delete(UUID id);
}
