package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {
    public User createUser(User user);
    public void findAllUsers();
    public User findUserById(UUID id);
    public void updateUser(UUID id, String updateUser);
    public void deleteUser(User user);

    public void joinChannel(User user, Channel channel);

    public void leaveChannel(User user, Channel channel);
}
