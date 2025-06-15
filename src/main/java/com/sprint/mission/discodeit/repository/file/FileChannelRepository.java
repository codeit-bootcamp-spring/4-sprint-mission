package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.file.FileService;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private static final Path directory = Paths.get(System.getProperty("user.dir"), "channel");
    private static final Path filePath = directory.resolve("channels.ser");
    private static final List<Channel> channels = new ArrayList<>();

    public FileChannelRepository() {
        FileService.init(directory);
    }

    // Create
    @Override
    public void addChannelAndSave(Channel channel, User hostUser) {
        hostUser.addChannel(channel);
        channel.addUserToActiveChannel(hostUser);
        channels.add(channel);

        save();
        FileUserRepository.save();
    }

    public static void save() {
        FileService.save(filePath, channels);
    }

    // Read
    @Override
    public List<Channel> getAll() {
        List<Channel> fileChannels = FileService.load(directory, Channel.class);
        return fileChannels;
    }

    @Override
    public Channel getById(UUID id) {
        return getAll().stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Channel with ID " + id + " not found from file."));
    }

    // Update
    @Override
    public void updateChannelName(Channel channel, String name) {
        if (!channels.contains(channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }

        channel.setChannelName(name);
        channel.setUpdatedAt(System.currentTimeMillis());

        save();
        synchroWithUserAndMessage(channel);
    }

    // Delete
    @Override
    public void delete(Channel channel, User hostUser) {
        if (!channels.contains(channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        channel.removeAllUsersAndMessagesFromActiveChannel();
        channels.remove(channel);

        save();
        synchroWithUserAndMessage(channel);
    }

    @Override
    public void deleteAll() {
        channels.forEach(Channel::removeAllUsersAndMessagesFromActiveChannel);
        channels.clear();

        FileUserRepository.save();
        FileMessageRepository.save();

        File file = new File(filePath.toUri());
        if (file.delete()) {
            System.out.println("<deleteAll() 성공: 전체 채널을 삭제하였습니다.>");
        } else {
            System.out.println("<deleteAll() 실패: 전체 채널 삭제에 실패하였습니다.>");
        }
    }

    private void synchroWithUserAndMessage(Channel channel) {
        if (channel.getMessages().isEmpty() && channel.getUsers().isEmpty()) { return; }
        FileUserRepository.save();
        FileMessageRepository.save();
    }
}
