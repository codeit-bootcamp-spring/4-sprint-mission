package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(UUID userId, String updatedText) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        user.setUserName(updatedText);
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
    }

    @Override
    public void printUsersByKeyword(String keyword) {
        System.out.println("[" + keyword + "] 키워드로 검색한 결과: ");
        for (User user : userRepository.findAll()) {
            if(user.getUserName().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(user);
            }
        }
    }

    @Override
    public void updateUserStatus (UUID userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

        user.setUserStatus(status);
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.save(user);
    }

    @Override
    public List<User> findUsersByStatus(UserStatus status) {
        List<User> result = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            // 상태가 일치하는 유저만 결과에 추가
            if(user.getUserStatus().equals(status)){
                result.add(user);
            }
        }
        return result;
    }
}
