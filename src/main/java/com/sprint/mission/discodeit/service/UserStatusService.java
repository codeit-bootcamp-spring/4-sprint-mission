package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto userStatusCreateDto);
    UserStatusResponseDto findById(UUID userStatusId);
    List<UserStatusResponseDto> findByAll();
    UserStatusResponseDto update(UserStatusUpdateDto userStatusUpdateDto);
    UserStatusResponseDto updateByUserId(UserStatusUpdateDto userStatusUpdateDto);
    void delete(UUID userStatusId);
}
