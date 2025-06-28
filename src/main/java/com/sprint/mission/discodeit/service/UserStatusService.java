package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusResponseDto create(UserStatusCreateDto userStatusCreateDto);

    UserStatusResponseDto find(UUID id);

    UserStatusResponseDto findByUserId(UUID userId);

    List<UserStatusResponseDto> findAll();

    UserStatusResponseDto update(UserStatusUpdateDto userStatusUpdateDto);

    UserStatusResponseDto updateByUserId(UserStatusUpdateDto userStatusUpdateDto, UUID userId);

    void delete(UUID id);
}
