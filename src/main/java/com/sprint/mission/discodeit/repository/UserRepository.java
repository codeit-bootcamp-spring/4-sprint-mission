package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    // Create
    public void addUserAndSave(User user);

    // Read
    public List<User> getAll();

    public User getById(UUID id);

    // Update
    public void updateName(User user, String name);

    public void updateStatus(User user, UserType.UserStatus status);

    // Delete
    public void delete(User user);

    public void deleteAll();

    // Channel join/out
    public void joinChannel(User user, Channel channel);

    public void outChannel(User user, Channel channel);

}
