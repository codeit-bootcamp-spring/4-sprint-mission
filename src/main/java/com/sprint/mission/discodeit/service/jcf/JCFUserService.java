package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User createUser(User user) {
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> getUserByUserId(UUID userId) {
        return data.stream()
                .filter(u -> u.getStatus().equals(UserStatus.ACTIVE))
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<User> getUsers() {
        return data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> updateUser(UUID userId, User updateUser) {
        Optional<User> OptionalUser = data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();

        Optional.ofNullable(updateUser.getDisplayName())
                        .ifPresent(name -> updateUser.updateUserDisplayName(name));

        Optional.ofNullable(updateUser.getStatus())
                .ifPresent(status -> updateUser.updateUserStatus(status));


        return OptionalUser;
    }


    @Override
    public void joinChannelToUser(User user, Channel channel) {
        user.addChannel(channel);
    }

    @Override
    public void deleteUserFromChannel(User user, Channel channel) {
        user.deleteChannel(channel);
    }


    @Override
    public void deleteUser(User user, UserStatus status) {
        data.removeIf(u -> u.getStatus() == UserStatus.DELETED && u.getId().equals(user.getId()));
    }
}
