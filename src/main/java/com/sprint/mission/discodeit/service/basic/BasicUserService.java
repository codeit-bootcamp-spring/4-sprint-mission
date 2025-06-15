package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;
import com.sprint.mission.discodeit.service.utility.UserValidator;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create
    @Override
    public User createUser(String name) {
        if (!DetectUtility.detect(name)) {
            name = "guest";
        }
        User user = new User(name);
        userRepository.addUserAndSave(user);
        return user;
    }

    // Read
    @Override
    public List<User> getUsers() {
        return userRepository.getAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.getById(id);
    }

    // Update
    @Override
    public void updateActiveUserNameByUser(User user, String name) {
        if (!DetectUtility.detect(name, user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        userRepository.updateName(user, name);
    }

    @Override
    public void updateUserStatusByUserExceptQuitUser(User user, UserType.UserStatus userStatus) {
        userRepository.updateStatus(user, userStatus);
    }

    // Delete
    @Override
    public void deleteUserByUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // Channel join/out
    @Override
    public void joinChannelOnlyActiveUser(User user, Channel channel) {
        if (!UserValidator.detectJoinChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        userRepository.joinChannel(user, channel);
    }

    @Override
    public void outChannelOnlyActiveUser(User user, Channel channel) {
        if (!UserValidator.detectOutChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        userRepository.outChannel(user, channel);
    }
}
