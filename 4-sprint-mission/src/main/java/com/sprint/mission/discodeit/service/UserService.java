package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import java.util.List;
import java.util.UUID;

public interface UserService {
    public UserResponseDto createUser(UserCreateDto userDto); // 유저 가입

    public List<UserResponseDto> findAllUser(); // 모든 유저 출력

    public UserResponseDto findUserById(UUID userId); // 특정 유저 출력

    public List<UserResponseDto> findUsersByNameContains(String userName); // 특정 단어가 들어가있는 유저 출력

    public UserResponseDto updateUser(UserUpdateDto userUpdateDto); // 유저 이름 수정

    public void deleteUser(UUID userId); // 유저 탈퇴

    public List<MessageResponseDto> findMessagesByUser(UUID userId); // 유저가 쓴 메세지 출력
}
