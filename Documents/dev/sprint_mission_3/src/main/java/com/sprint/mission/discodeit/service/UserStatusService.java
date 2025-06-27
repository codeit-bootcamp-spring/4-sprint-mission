package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto dto);
    UserStatusResponseDto find(UUID id);
    List<UserStatusResponseDto> findAllByUserId(UUID userId);
    UserStatusResponseDto update(UserStatusUpdateDto dto);
    UserStatusResponseDto updateByUserId(UserStatusUpdateDto dto);
    void delete(UUID id);
}
