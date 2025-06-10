package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFuserService implements UserService {
    private final Set<User> data;

    public JCFuserService() {
        this.data = new HashSet<>();
    }


    @Override
    public User createUser(String username, String email, String phone) {
        User user = new User(username, email, phone);
        data.add(user);
        return user;
    }

    @Override
    public User getUserById(String userId) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        } return null;
    }

    @Override
    public Set<User> getAllUsers() {
        return new HashSet<>(data);
    }

    @Override
    public User updateUser(String userId, String newUsername, String newEmail, String newPhone) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                user.setUsername(newUsername);
                user.setEmail(newEmail);
                user.setPhone(newPhone);
                user.setUpdatedAt(System.currentTimeMillis());

                return user;
            }
        }
        return null;
    }

    public User deleteUser(String userId) {
        for (User user : data) {
            if (user.getUserId().equals(userId)) {
                data.remove(user);
                return user;
            }
        }
        return null;
    }
}
