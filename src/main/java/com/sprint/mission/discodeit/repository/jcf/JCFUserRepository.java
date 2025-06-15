package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private static final List<User> users = new ArrayList<>();

    // Create
    @Override
    public void addUserAndSave(User user) {
        users.add(user);
    }

    // Read
    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User getById(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

    // Update
    @Override
    public void updateName(User user, String name) {
        user.setUserName(name);
        user.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void updateStatus(User user, UserType.UserStatus userStatus) {
        user.setStatus(userStatus);
        user.setUpdatedAt(System.currentTimeMillis());
    }

    // Delete
    @Override
    public void delete(User user) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.removeAllChannelsAndMessages();
        users.remove(user);
    }

    @Override
    public void deleteAll() {
        users.forEach(User::removeAllChannelsAndMessages);
        users.clear();
    }

    // Channel join/out
    @Override
    public void joinChannel(User user, Channel channel) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.addChannel(channel);
        channel.addUserToActiveChannel(user);
    }

    @Override
    public void outChannel(User user, Channel channel) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.outFromChannel(channel);
        channel.removeUserByUserIdFromActiveChannel(user.getId());
    }

    public static boolean detectJoinChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean notJoined = !user.getChannels().contains(channel);
        return detected && notJoined;
    }

    public static boolean detectOutChannel(User user, Channel channel) {
        boolean detected = DetectUtility.detect(user) && DetectUtility.detect(channel);
        boolean joined = user.getChannels().contains(channel);
        return detected && joined;
    }
}
