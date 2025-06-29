package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserStatusRequest;
import com.sprint.mission.discodeit.DTO.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusResponse create(UserStatusRequest request);
    UserStatusResponse findById(UUID id);
    List<UserStatusResponse> findAll();
    UserStatusResponse update(UserStatusRequest request);
    UserStatusResponse updateByUserId(UserStatusRequest request);
    void delete(UUID id);




}
