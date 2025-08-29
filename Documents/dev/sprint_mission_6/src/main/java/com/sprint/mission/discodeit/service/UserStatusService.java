package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(UserStatusCreateDto dto);
    UserStatusDto find(UUID id);
    List<UserStatusDto> findAllByUserId(UUID userId);
    UserStatusDto update(UserStatusUpdateDto dto);
    UserStatusDto updateByUserId(UserStatusUpdateDto dto);
    void delete(UUID id);
}
