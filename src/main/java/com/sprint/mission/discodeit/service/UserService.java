package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

    public interface UserService {
        public void addUser(User user);
        public List<User> getUsers();
        public void deleteUser(UUID userId);
        public void updateUser(UUID userId, String updatedText );
        public User getUserById(UUID userId);
        public void findUsersByKeyword(String keyword);
        public void updateUserStatus(UUID userId, UserStatus status);
        public List<User> findUsersByStatus(UserStatus status);
    }
