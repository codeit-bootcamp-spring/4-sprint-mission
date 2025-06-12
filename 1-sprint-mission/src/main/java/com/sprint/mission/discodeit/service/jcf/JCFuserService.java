package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFuserService implements UserService {
    private final Map<String, User> data;
    private static JCFuserService instance = new JCFuserService();

    private JCFuserService() {
        this.data = new HashMap<>();
    }


    public static JCFuserService getInstance() {
        return instance;
    }


    @Override
    public User createUser(User user) {
        data.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User getUserById(String userId) {
        return data.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(String userId, String newUsername, String newEmail, String newPhone) {
        User user = data.get(userId);
        if (user != null) {
            user.setUsername(newUsername);
            user.setEmail(newEmail);
            user.setPhone(newPhone);
            user.setUpdatedAt(System.currentTimeMillis());
        }
        return user;
    }

    public User deleteUser(String userId) {
        return data.remove(userId);
    }
}
