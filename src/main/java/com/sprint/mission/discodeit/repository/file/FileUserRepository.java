package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.domain.UserType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.file.FileService;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private static final Path directory = Paths.get(System.getProperty("user.dir"), "user");
    private static final Path filePath = directory.resolve("users.ser");
    private static final List<User> users = new ArrayList<>();

    public FileUserRepository() {
        FileService.init(directory);
    }

    // Create
    @Override
    public void addUserAndSave(User user) {
        users.add(user);
        save();
    }

    public static void save() {
        FileService.save(filePath, users);
    }

    // Read
    @Override
    public List<User> getAll() {
        List<User> fileUsers = FileService.load(directory, User.class);
        return fileUsers;
    }

    @Override
    public User getById(UUID id) {
        return getAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found from channel."));
    }

    // Update
    @Override
    public void updateName(User user, String name) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }

        user.setUserName(name);
        user.setUpdatedAt(System.currentTimeMillis());
        save();
        synchroWithChannelAndMessage(user);
    }

    @Override
    public void updateStatus(User user, UserType.UserStatus userStatus) {
        if (!users.contains(user)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }

        user.setStatus(userStatus);
        user.setUpdatedAt(System.currentTimeMillis());
        save();
        synchroWithChannelAndMessage(user);
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

        save();
        synchroWithChannelAndMessage(user);
    }


    @Override
    public void deleteAll() {
        users.forEach(User::removeAllChannelsAndMessages);
        users.clear();

        FileChannelRepository.save();
        FileMessageRepository.save();

        File file = new File(filePath.toUri());
        if (file.delete()) {
            System.out.println("<deleteAll() 성공: 전체 유저를 삭제하였습니다.>");
        } else {
            System.out.println("<deleteAll() 실패: 전체 유저 삭제에 실패하였습니다.>");
        }
    }

    // Channel join/out
    @Override
    public void joinChannel(User user, Channel channel) {
        user.addChannel(channel);
        channel.addUserToActiveChannel(user);
        save();
        FileChannelRepository.save();
    }

    @Override
    public void outChannel(User user, Channel channel) {
        user.outFromChannel(channel);
        channel.removeUserByUserIdFromActiveChannel(user.getId());
        save();
        FileChannelRepository.save();
    }

    private void synchroWithChannelAndMessage(User user) {
        if (user.getChannels().isEmpty() && user.getMessages().isEmpty()) { return; }
        FileChannelRepository.save();
        FileMessageRepository.save();
    }
}
