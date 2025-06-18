package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sprint.mission.discodeit.entity.User;  // 또는 model.User 등 실제 위치에 맞게

public interface UserRepository {
    Optional <User> findById(UUID userId);
    void save(User user);
    void deleteById(UUID userId);
    List<User> findAll();
}
