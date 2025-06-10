package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.Set;

public interface UserService {
    User createUser(String username, String email, String phone);
    User getUserById(String userId);
    Set<User> getAllUsers();
    User updateUser(String userId, String newUsername, String newEmail, String newPhone);
    User deleteUser(String userId);
}
