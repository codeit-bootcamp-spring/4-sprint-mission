package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserStatusResponseDto create(UserCreateDto userCreateDto);

    UserStatusResponseDto find(UUID userId);

    List<UserStatusResponseDto> findAll();

    User update(UserUpdateDto userUpdateDto);

    void delete(UUID userId);

    boolean isOnline(UserStatusResponseDto userStatusDto);
}
