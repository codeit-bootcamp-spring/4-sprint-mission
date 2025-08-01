package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateServiceRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {

    public UserDto createUser(UserCreateServiceRequest userDto); // 유저 가입

    public List<UserDto> findAllUser(); // 모든 유저 출력

    public UserDto updateUser(UUID userId, UserUpdateServiceRequest request); // 유저 이름 수정

    public void deleteUser(UUID userId); // 유저 탈퇴
}
