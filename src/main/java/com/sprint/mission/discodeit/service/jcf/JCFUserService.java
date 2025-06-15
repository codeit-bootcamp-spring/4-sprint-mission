package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;
import com.sprint.mission.discodeit.service.utility.UserValidator;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    private static final JCFUserRepository jcfUserRepository = new JCFUserRepository();

    // Create
    @Override
    public User createUser(String name) {
        if (!DetectUtility.detect(name)) {
            name = "guest";
        }
        User user = new User(name);
        jcfUserRepository.addUserAndSave(user);
        return user;
    }

    // Read
    @Override
    public List<User> getUsers() {
        return jcfUserRepository.getAll();
    }

    @Override
    public User getUserById(UUID id) {
        return jcfUserRepository.getById(id);
    }

    // Update
    @Override
    public void updateActiveUserNameByUser(User user, String name) {
        if (!DetectUtility.detect(name, user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.updateName(user, name);
    }

    @Override
    public void updateUserStatusByUserExceptQuitUser(User user, UserType.UserStatus userStatus) {
        if (user.getStatus() == UserType.UserStatus.QUIT) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.updateStatus(user, userStatus);
    }

    // Delete
    @Override
    public void deleteUserByUser(User user) {
        jcfUserRepository.delete(user);
    }

    @Override
    public void deleteAllUsers() {
        jcfUserRepository.deleteAll();
    }

    // 참여중인 채널 관련 메서드
    // 채널 참가
    @Override
    public void joinChannelOnlyActiveUser(User user, Channel channel) {
        if (!UserValidator.detectJoinChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.joinChannel(user, channel);
    }

    // 채널 나가기
    @Override
    public void outChannelOnlyActiveUser(User user, Channel channel) {
        if (!UserValidator.detectOutChannel(user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfUserRepository.outChannel(user, channel);
    }
}
