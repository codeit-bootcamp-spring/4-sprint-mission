package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(UserCreateRequest user);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    UserDto update(UserUpdateRequest userUpdateRequest);
    void delete(UUID userId);
}
