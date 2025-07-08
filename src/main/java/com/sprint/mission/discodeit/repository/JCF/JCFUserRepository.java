package com.sprint.mission.discodeit.repository.JCF;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> userData;

    public JCFUserRepository() {
        userData = new HashMap<>();
    }

    @Override
    public User save(User user) {
        this.userData.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUser(UUID id) {
        return Optional.ofNullable(userData.get(id));
    }

    @Override
    public List<User> findAll() {
        return this.userData.values().stream().toList();
    }

    @Override
    public boolean existsUser(UUID id) {
        return this.userData.containsKey(id);
    }

    @Override
    public void deleteUser(UUID id) {
        this.userData.remove(id);
    }
}
