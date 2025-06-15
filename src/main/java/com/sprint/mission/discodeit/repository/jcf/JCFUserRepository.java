package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.nio.file.Path;
import java.util.*;

public class JCFUserRepository implements UserRepository {
    private static JCFUserRepository jcfUserRepository;
    private final List<User> data;

    public JCFUserRepository() {
        data = new ArrayList<>();
    }

    public static JCFUserRepository getInstance() {
        if (jcfUserRepository == null) {
            jcfUserRepository = new JCFUserRepository();
        }
        return jcfUserRepository;
    }

    @Override
    public User saveUser(Path dir, User user) {
        data.add(user);
        return user;
    }

    @Override
    public List<User> loadAllUsers(Path dir) {
        return data.stream()
                .toList();
    }

    @Override
    public Optional<User> loadUserById(Path dir, UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> loadUserByUserName(Path dir, String userName) {
        return data.stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst();
    }

    @Override
    public void deleteUser(Path dir, User user) {
        data.remove(user);
    }

    @Override
    public void joinChannel(Path userDir, Path channelDir, User user, Channel channel) {
        JCFChannelRepository jcfChannelRepository = JCFChannelRepository.getInstance();

        user.addChannel(channel);
        channel.addUser(user);
    }

    @Override
    public void leaveChannel(Path userDir, Path channelDir, User user, Channel channel) {
        JCFChannelRepository jcfChannelRepository = JCFChannelRepository.getInstance();

        user.removeChannel(channel);
        channel.removeUser(user);
    }
}

