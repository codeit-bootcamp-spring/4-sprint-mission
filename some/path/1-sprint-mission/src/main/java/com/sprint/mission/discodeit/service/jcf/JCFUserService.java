package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User addUser(User user) {
        data.add(user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return data.stream()
                .filter(user -> user.getState()!=UserState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserById(UUID UserId) {
        return data.stream()
                .filter(user -> user.getUserId().equals(UserId))
                .filter(user -> user.getState()!=UserState.DELETED)
                .findFirst();
    }

    @Override
    public List<User> findUsersByNameContains(String UserName) {
        return data.stream()
                .filter(user -> user.getUserName().contains(UserName))
                .filter(user -> user.getState()!=UserState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(UUID userId, String updatedText) {
        data.stream()
                .filter(user -> user.getState()!=UserState.DELETED)
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .ifPresent(user -> {
                    user.setUserName(updatedText);
                    user.setUpdatedAt();
                });
    }

    @Override
    public void deleteUser(User user) {
        if(user.getState()!=UserState.DELETED) {
            user.deletedUserState();
            data.remove(user);
        }
    }

    @Override
    public List<Message> findMessagesByUser(User user) {
        return user.getMessages().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

}
