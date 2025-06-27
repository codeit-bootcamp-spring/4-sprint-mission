package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user_status_dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto createUserStatus(CreateUserStatusRequestDto createUserStatusRequestDto);
    UserStatusResponseDto updateUserStatus(UpdateUserStatusRequestDto updateUserStatusRequestDto);

    List<UserStatusResponseDto> findAllUserStatus();

    void deleteUserStatusByUserId(UUID userId);
    void deleteUserStatusByUserStatusId(UUID userStatusId);

    UserStatusResponseDto findUserStatusByUserId(UUID userId);
}
