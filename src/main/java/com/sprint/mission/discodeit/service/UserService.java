package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 등록
    User createUser(String name);

    // 단건 조회
    User findUserById(UUID id);
    // 다건 조회
    List<User> findAllUsers();

    // 수정
    User updateUserById(UUID id, String newName);

    // 삭제
    User deleteUserById(UUID id);
}
