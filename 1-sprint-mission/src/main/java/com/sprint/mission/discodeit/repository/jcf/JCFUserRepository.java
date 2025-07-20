package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JCFUserRepository implements UserRepository {

    private final Map<String, User> data = new HashMap<>();

    @Override
    public User save(User user) {
        data.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User findById(String userId) {

        User user = data.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("Channel not found");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User delete(String userId) {
        User remove = data.remove(userId);
        if (remove == null) {
            throw new IllegalArgumentException("User not found");
        } else {
            remove.getMessages().clear();
        }
        return remove;
    }
}
