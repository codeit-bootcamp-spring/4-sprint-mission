package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    public User createUser(String name, String password, String email);
    public List<User> getUsers();
    public Optional<User> getUsersById(UUID userId);
    public void updateUser(UUID userId, User updateUser);
    public void deleteUser(UUID userId);
    public void leaveChannel(User user, Channel channel);
}
