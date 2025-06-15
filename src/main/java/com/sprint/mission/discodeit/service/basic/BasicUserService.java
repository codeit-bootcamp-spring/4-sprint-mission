package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class BasicUserService {
    UserRepository userRepository;

    private BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static BasicUserService basicUserService;

    public static BasicUserService getInstance(UserRepository userRepository) {
        if(basicUserService == null){
            basicUserService = new BasicUserService(userRepository);
        }
        return basicUserService;
    }


    public User saveUser(Path dir, User user) {
        return userRepository.saveUser(dir, user);
    }

    public List<User> loadAllUsers(Path dir) {
        return userRepository.loadAllUsers(dir);
    }

    public Optional<User> loadUserById(Path dir, UUID id) {
        return userRepository.loadUserById(dir, id);

    }
    public Optional<User> loadUserByUsername(Path dir, String username) {
        return userRepository.loadUserByUserName(dir, username);
    }

    public void updateUserById(Path dir, UUID id, String updateUserName) {
        loadUserById(dir, id).ifPresent(user -> {
            user.setUserName(updateUserName);
            user.setUpdatedAt(System.currentTimeMillis());
            saveUser(dir, user);
        });
    }

    public void updateUserByUserName(Path dir, String userName, String updateUserName) {
        loadUserByUsername(dir, userName).ifPresent(user -> {
            user.setUserName(updateUserName);
            user.setUpdatedAt(System.currentTimeMillis());
            saveUser(dir, user);
        });
    }

    public void deleteUser(Path dir, User user) {
        userRepository.deleteUser(dir, user);
    }

    public void joinChannel(Path userDir, Path channelDir, User user, Channel channel) {
        userRepository.joinChannel(userDir, channelDir, user, channel);
    }

    public void leaveChannel(Path userDir, Path channelDir, User user, Channel channel) {
        userRepository.leaveChannel(userDir, channelDir, user, channel);
    }
}