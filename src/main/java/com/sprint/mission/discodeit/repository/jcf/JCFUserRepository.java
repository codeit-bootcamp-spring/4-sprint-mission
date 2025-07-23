package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JCFUserRepository implements UserRepository {
    private List<User> data = new ArrayList<>();

    public List<User> findAll() {
        return data;
    }

    @Override
    public User findById(UUID id) {
        User user = data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        return user;
    }

    @Override
    public User findByName(String name) {
        User user = data.stream()
                .filter(u -> u.getUsername().equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        return user;
    }

    @Override
    public void save(User user) {
        if (data.contains(user)) {
            data.stream()
                    .map(u -> u.equals(user) ? user : u)
                    .forEach(u -> {
                    });
        } else {
            data.add(user);
        }
    }

    @Override
    public void delete(User user) {
        data.remove(user);
    }


}
