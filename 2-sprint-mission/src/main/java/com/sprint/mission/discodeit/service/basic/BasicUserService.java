package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(String name) {
        User user = new User(name);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(UUID UserId) {
        return userRepository.findById(UserId);
    }

    @Override
    public List<User> findUsersByNameContains(String UserName) {
        return userRepository.findUsersByNameContains(UserName);
    }

    @Override
    public void updateUser(UUID userId, String updatedText) {
        User findUser = isExistUser(userId);
        findUser.setUserName(updatedText);
        userRepository.save(findUser);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<Message> findMessagesByUser(User user) {
        User findUser = isExistUser(user);
        return findUser.getMessages().stream()
                .filter(message -> message.getState()!= MessageState.DELETED)
                .collect(Collectors.toList());
    }

    public User isExistUser(User user) {
        return userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
    public User isExistUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}
