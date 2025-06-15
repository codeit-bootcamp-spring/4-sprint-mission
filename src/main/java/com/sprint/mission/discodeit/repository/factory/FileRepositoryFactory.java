package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.file.FileIOHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileRepositoryFactory implements RepositoryFactory {

    // 파일 경로를 초기화하는 메서드
    private Path initializePath(String fileName) {
        Path directory = Paths.get(System.getProperty("user.dir"), "data");
        FileIOHelper.init(directory);
        return directory.resolve(fileName);
    }

    @Override
    public UserRepository CreateUserRepository() {
        Path userFilePath = initializePath("users.ser");
        return FileUserRepository.getInstance(userFilePath);
    }

    @Override
    public ChannelRepository CreateChannelRepository() {
        Path channelFilePath = initializePath("channels.ser");
        return FileChannelRepository.getInstance(channelFilePath);
    }

    @Override
    public MessageRepository CreateMessageRepository() {
        Path messageFilePath = initializePath("messages.ser");
        return FileMessageRepository.getInstance(messageFilePath);
    }
}
