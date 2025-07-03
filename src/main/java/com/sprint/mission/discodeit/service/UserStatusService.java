package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto createUserStatus(UUID userId);
    UserStatusResponseDto updateUserStatus(UUID userId);

    List<UserStatusResponseDto> findAllUserStatus();

    void deleteUserStatusByUserId(UUID userId);
    void deleteUserStatusByUserStatusId(UUID userStatusId);

    UserStatusResponseDto findUserStatusByUserId(UUID userId);
}
