package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    public User save(User user);
    public Optional<User> findUser(UUID id);
    public List<User> findAll();
    public boolean existsUser(UUID id);
    public void deleteUser(UUID id);
}
