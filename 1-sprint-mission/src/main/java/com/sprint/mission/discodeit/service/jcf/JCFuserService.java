package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFuserService implements UserService {

    private static JCFuserService instance = new JCFuserService();

    private final UserRepository userRepository;

    private JCFuserService() {
        this.userRepository = new JCFUserRepository();
    }

    public static JCFuserService getInstance() {
        return instance;
    }


    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(String userId, String newUsername, String newEmail, String newPhone) {
        User user = userRepository.findById(userId);

        if (user != null) {
            user.setUsername(newUsername);
            user.setEmail(newEmail);
            user.setPhone(newPhone);
            user.setUpdatedAt(System.currentTimeMillis());

            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @Override
    public User deleteUser(String userId) {
        return userRepository.delete(userId);
    }
}
