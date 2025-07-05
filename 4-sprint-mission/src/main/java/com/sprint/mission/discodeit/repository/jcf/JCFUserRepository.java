package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.AccountState;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        User user = data.get(userId);
        if (user == null || user.getState() == AccountState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        return data.values().stream()
                .filter(user -> user.getState() != AccountState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        User existing = data.get(id);
        if (existing != null && existing.getState() != AccountState.DELETED) {
            existing.setToDeleted();
        }
    }

    @Override
    public List<User> findUsersByNameContains(String name) {
        return data.values().stream()
                .filter(user -> user.getUserName().contains(name))
                .filter(user -> user.getState() != AccountState.DELETED)
                .collect(Collectors.toList());
    }
}
