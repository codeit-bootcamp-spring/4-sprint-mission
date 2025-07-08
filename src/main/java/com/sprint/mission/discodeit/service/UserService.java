package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UpdateUserDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto createUser (UserDto userDto);
    UserDto findUser(UUID id);
    List<UserDto> findAll();
    UserDto updateUser(UUID id, UpdateUserDto updateUserDTO);
    void deleteUser(UUID id);
}
