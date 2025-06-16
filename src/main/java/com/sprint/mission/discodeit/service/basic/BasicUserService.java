package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.factory.RepositoryFactory;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName) {
        return userRepository.createUser(userName);
    }

    @Override
    public void updateUser(User user, String newName) {
        userRepository.updateUser(user, newName);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.deleteUser(user);
    }

    @Override
    public void restoreUser(User user) {
        userRepository.restoreUser(user);
    }

    @Override
    public void printActiveUsers(){
        // status를 판별하는 식을 추가
        List<User> data = userRepository.getUsers();

        System.out.printf("전체 유저 조회(탈퇴 유저 미포함), 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printUser(User user) {
        System.out.print("단일 유저 조회\n");
        System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
    }

    @Override
    public void printAllUsers() {
        List<User> data = userRepository.getUsers();
        System.out.printf("전체 유저 조회(탈퇴 유저 포함), 유저 수: %d \n", data.size());
        data
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printDeactivatedUsers() {
        List<User> data = userRepository.getUsers();
        System.out.printf("탈퇴 유저 조회, 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.DEACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.DEACTIVE)) // getStatus는 boolean 값이므로 equal 생략
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }
}
