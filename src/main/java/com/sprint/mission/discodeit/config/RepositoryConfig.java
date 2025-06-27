package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {

    private final DiscodeitRepositoryProperties properties;

    private boolean useFile() {
        return "file".equalsIgnoreCase(properties.getType());
    }

    @Bean
    public UserRepository userRepository() {
        return useFile() ?
                new FileUserRepository(properties.getFileDirectory()) :
                new JcfUserRepository();
    }

    @Bean
    public BinaryContentsRepository binaryContentsRepository() {
        return useFile() ?
                new FileBinaryContentsRepository(properties.getFileDirectory()) :
                new JcfBinaryContentsRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        return useFile() ?
                new FileChannelRepository(properties.getFileDirectory()) :
                new JcfChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        return useFile() ?
                new FileMessageRepository(properties.getFileDirectory()) :
                new JcfMessageRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return useFile() ?
                new FileReadStatusRepository(properties.getFileDirectory()) :
                new JcfReadStatusRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return useFile() ?
                new FileUserStatusRepository(properties.getFileDirectory()) :
                new JcfUserStatusRepository();
    }
}