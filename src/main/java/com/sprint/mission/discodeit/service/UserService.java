package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;

public interface UserService {

    void updateUser(UserUpdateRequestDto userUpdateRequestDTO);
    void deleteUser(UserResponseDto userResponseDto);
    void restoreUser(String userName);
    UserResponseDto createUser(UserCreateRequestDto userCreateRequestDTO);

    List<UserResponseDto> findAllUserDTO();
    List<UserResponseDto> findAllActiveUserDTO();
    List<UserResponseDto> findAllDeactiveUserDTO();


}
