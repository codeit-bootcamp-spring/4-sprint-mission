package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserRepository {

    User createUser(String userName);
    void updateUser(User user, String newUserName);
    void deleteUser(User user);
    void restoreUser(User user);

    List<User> getUsers();
}
