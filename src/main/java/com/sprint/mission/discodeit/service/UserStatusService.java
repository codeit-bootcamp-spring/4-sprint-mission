package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto.*;
import java.util.UUID;

public interface UserStatusService {

  UserStatusResponse create(UserStatusRequest userStatusRequest);

  UserStatusResponse find(UUID userStatusId);

  UserStatusResponse findByUserId(UUID userId);

  UserStatusResponses findAll();

  UserStatusUpdateResponse update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatusUpdateResponse updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID id);

  void deleteAll();
}
