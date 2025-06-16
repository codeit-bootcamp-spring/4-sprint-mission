package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {

    void updateUser(User user, String newUserName);
    void deleteUser(User user);
    void restoreUser(User user);
    User createUser(String userName);

    void printUser(User user);
    void printAllUsers();
    void printActiveUsers();
    void printDeactivatedUsers();
}
