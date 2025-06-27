package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto dto);
    UserStatusResponseDto find(UUID id);
    List<UserStatusResponseDto> findAll();
    UserStatusResponseDto update(UserStatusUpdateDto dto);
    UserStatusResponseDto updateByUserId(UUID userId);
    void delete(UUID id);
}
