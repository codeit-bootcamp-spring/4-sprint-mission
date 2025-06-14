package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRepository {
    User save(User user);
    User findById(String userId);
    List<User> findAll();
    User delete(String userId);
}
