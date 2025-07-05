package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DiscodeitProperties.class)
@RequiredArgsConstructor
public class RepositoryConfig {
    private final DiscodeitProperties properties;
    private static final String FILE_TYPE = "file";

    @Bean
    public UserRepository userRepository() {
        if (FILE_TYPE.equalsIgnoreCase(properties.getType())) {
            return new FileUserRepository(properties.getFileDirectory());
        }
        return new JCFUserRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        if (FILE_TYPE.equalsIgnoreCase(properties.getType())) {
            return new FileChannelRepository(properties.getFileDirectory());
        }
        return new JCFChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        if (FILE_TYPE.equalsIgnoreCase(properties.getType())) {
            return new FileMessageRepository(properties.getFileDirectory());
        }
        return new JCFMessageRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        if (FILE_TYPE.equalsIgnoreCase(properties.getType())) {
            return new FileBinaryContentRepository(properties.getFileDirectory());
        }
        return new JCFBinaryContentRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        if (FILE_TYPE.equalsIgnoreCase(properties.getType())) {
            return new FileReadStatusRepository(properties.getFileDirectory());
        }
        return new JCFReadStatusRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        if (FILE_TYPE.equalsIgnoreCase(properties.getType())) {
            return new FileUserStatusRepository(properties.getFileDirectory());
        }
        return new JCFUserStatusRepository();
    }
}
