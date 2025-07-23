package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    List<User> findAll();
    User findById(UUID id);
    void delete(User user);
    void save(User user);
    User findByName(String name);
}
