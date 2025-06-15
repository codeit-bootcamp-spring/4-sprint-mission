package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;
import com.sprint.mission.discodeit.service.utility.UserValidator;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private static final FileUserRepository fileUserRepository = new FileUserRepository();

    // Create
    @Override
    public User createUser(String name) {
        if (!DetectUtility.detect(name)) {
            name = "guest";
        }
        User user = new User(name);
        fileUserRepository.addUserAndSave(user);
        return user;
    }

    // Read
    @Override
    public List<User> getUsers() {
        return fileUserRepository.getAll();
    }

    @Override
    public User getUserById(UUID id) {
        return fileUserRepository.getById(id);
    }

    // Update
    @Override
    public void updateActiveUserNameByUser(User user, String name) {
        if (!DetectUtility.detect(name, user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileUserRepository.updateName(user, name);
    }

    @Override
    public void updateUserStatusByUserExceptQuitUser(User user, UserType.UserStatus userStatus) {
        fileUserRepository.updateStatus(user, userStatus);
    }

    // Delete
    @Override
    public void deleteUserByUser(User user) {
        fileUserRepository.delete(user);
    }

    @Override
    public void deleteAllUsers() {
        fileUserRepository.deleteAll();
    }

    // Channel join/out
    @Override
    public void joinChannelOnlyActiveUser(User user, Channel channel) {
        if (!UserValidator.detectJoinChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileUserRepository.joinChannel(user, channel);
    }

    @Override
    public void outChannelOnlyActiveUser(User user, Channel channel) {
        if (!UserValidator.detectOutChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileUserRepository.outChannel(user, channel);
    }
}
