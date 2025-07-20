package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileUserService implements UserService{

    private static final FileUserService instance = new FileUserService();
    private final UserRepository userRepository = new FileUserRepository();

    private FileUserService() {}

    public static FileUserService getInstance() {
        return instance;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public User getUserById(String userId) {

        return userRepository.findById(userId);
    }

    @Override
    public User updateUser(String userId, String newUsername, String newEmail, String newPhone) {
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);
        user.setPhone(newPhone);
        user.setUpdatedAt(System.currentTimeMillis());

        // 중복 저장을 방지하려면 기존 데이터를 제거하고 저장
        userRepository.delete(userId);
        return userRepository.save(user);
    }

    public User deleteUser(String userId) {
        return userRepository.delete(userId);
    }

}