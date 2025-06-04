package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(User user);
    Optional<User> getUserByUserId(UUID userId);
    void joinChannelToUser(User user, Channel channel);
    void deleteUserFromChannel(User user, Channel channel);
    List<User> getUsers();
    Optional<User> updateUser(UUID userId, User updateUser);
    void deleteUser(User user, UserStatus status);
}
