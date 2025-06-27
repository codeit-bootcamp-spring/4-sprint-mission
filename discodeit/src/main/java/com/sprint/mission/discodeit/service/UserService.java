package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateDto userCreateDto);
    UserResponseDto findById(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UserUpdateDto userUpdateDto);
    void delete(UUID userId);
}
