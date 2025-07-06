package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserDto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserDto.UserUpdateDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentCreateDto;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateDto dto, @Nullable BinaryContentCreateDto binaryDto);
    UserResponseDto findById(UUID userId);
    List<UserResponseDto> findAll();
    UserResponseDto update(UserUpdateDto userUpdateDto);
    void delete(UUID userId);
}
