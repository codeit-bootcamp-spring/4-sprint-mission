package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private static final JCFUserRepository instance = new JCFUserRepository();
    private JCFUserRepository() {}
    public static JCFUserRepository getInstance() {
        return instance;
    }
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    @Override
    public void deleteById(UUID userId) {
        users.remove(userId);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
