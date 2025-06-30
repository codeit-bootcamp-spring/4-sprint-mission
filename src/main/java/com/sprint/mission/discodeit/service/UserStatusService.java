package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto userStatusCreateDto);
    UserStatusResponseDto find(UUID id);
    List<UserStatusResponseDto> findAll();
    UserStatusResponseDto update(UserStatusUpdateDto userStatusUpdateDto);
    UserStatusResponseDto updateByUserId(UUID userId);
    void delete(UUID id);
}
