package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;

public interface UserStatusService {

    UserStatusResponseDto create(UserStatusCreateDto dto);
}
