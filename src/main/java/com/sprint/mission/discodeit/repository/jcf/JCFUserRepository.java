package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {
    private static JCFUserRepository instance;
    private final Map<UUID, User> data = new HashMap<>();

    private JCFUserRepository() {}

    public static  JCFUserRepository getInstance() {
        if (instance == null) {
            instance = new JCFUserRepository();
        }
        return instance;
    }

    @Override
    public User save(User user) {
        data.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return data.get(userId);
    }

    @Override
    public List<User> findByName(String userName) {
        return data.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean isContains(UUID userId) {
        return data.containsKey(userId);
    }
}
