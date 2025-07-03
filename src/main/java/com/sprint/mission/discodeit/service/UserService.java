package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.dto.user_status_dto.*;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService {

    void updateUser(UserUpdateRequestDto userUpdateRequestDTO);
    void deleteUser(UserResponseDto userResponseDto);
    void restoreUser(String userName);
    UserResponseDto createUser(UserCreateRequestDto userCreateRequestDTO) throws IOException;


    UserResponseDto findUserDtoByUserName(String userName);
    UserResponseDto findUserDtoByUserId(UUID userId);
    List<UserResponseDto> findAllUserDTO();
    List<UserResponseDto> findAllActiveUserDTO();
    List<UserResponseDto> findAllDeactiveUserDTO();
    List<UserWithStatusResponseDto> findAllUserAndUserStatus();
    List<UserDto> findAllUserDto();

}
