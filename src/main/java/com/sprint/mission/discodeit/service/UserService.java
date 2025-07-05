package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserDto.create request);

    UserDto.response find(UUID userId);

    List<UserDto.response> findAll();

    User update(UUID userId, UserDto.update request);

    void delete(UUID userId);
}
