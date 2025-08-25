package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {

    // 사용자 추가
    void addUser(User user);
    // 사용자 정보 업데이트
    void updateUser(User user, String newUserName);
    // 사용자 삭제
    void deleteUser(User user);
    void restorationUser(User user);
    void printUser(User user);
    void printAllUsers();
    void printActiveUsers();
    void printDeactivatedUsers();
    User createUser(String userName);
}
