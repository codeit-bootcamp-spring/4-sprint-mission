package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User saveUser(Path dir, User user);

    List<User> loadAllUsers(Path dir);
    Optional<User> loadUserById(Path dir, UUID id);
    Optional<User> loadUserByUserName(Path dir, String userName);

    void deleteUser(Path dir, User user);

    void joinChannel(Path userDir, Path channelDir, User user, Channel channel);
    void leaveChannel(Path userDir, Path channelDir, User user, Channel channel);
}
