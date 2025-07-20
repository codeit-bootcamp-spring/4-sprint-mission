package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequest user);
    UserResponseDto find(UUID userId);
    List<UserDto> findAll();
    UserResponseDto update(UserUpdateRequest userUpdateRequest);
    void delete(UUID userId);
}
