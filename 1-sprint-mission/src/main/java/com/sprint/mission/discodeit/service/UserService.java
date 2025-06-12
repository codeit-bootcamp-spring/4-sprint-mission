package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User createUser(User user);
    User getUserById(String userId);
    List<User> getAllUsers();
    User updateUser(String userId, String newUsername, String newEmail, String newPhone);
    User deleteUser(String userId);
}
