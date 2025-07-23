package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserCreateResponseDto createUser(UserCreateRequest userCreateRequest, BinaryContentPostDto binaryContentPostDto);

    List<UserResponseDto> findAllUsers();

    UserResponseDto findUserById(UUID userId);

    UserResponseDto getUserByName(String name);

    UserResponseDto updateUser(UUID userId, UserUpdateRequest userUpdateRequest, BinaryContentPostDto binaryContentPostDto);

    void deleteUser(UUID userId);

    List<UserDto> findAll();
}
