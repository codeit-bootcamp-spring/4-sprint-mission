package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest);
    void delete(UUID userId);
}
