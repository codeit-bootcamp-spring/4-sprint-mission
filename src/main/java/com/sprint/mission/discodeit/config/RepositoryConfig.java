package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RepositoryConfig {

    private final RepositoryProperties repositoryProperties;

    // File Repositories
    private final FileBinaryContentRepository fileBinaryContentRepository;
    private final FileChannelRepository fileChannelRepository;
    private final FileMessageRepository fileMessageRepository;
    private final FileReadStatusRepository fileReadStatusRepository;
    private final FileUserRepository fileUserRepository;
    private final FileUserStatusRepository fileUserStatusRepository;

    // JCF Repositories
    private final JCFBinaryContentRepository jcfBinaryContentRepository;
    private final JCFChannelRepository jcfChannelRepository;
    private final JCFMessageRepository jcfMessageRepository;
    private final JCFReadStatusRepository jcfReadStatusRepository;
    private final JCFUserRepository jcfUserRepository;
    private final JCFUserStatusRepository jcfUserStatusRepository;

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        return switch (repositoryProperties.getType()) {
            case FILE -> fileBinaryContentRepository;
            case JCF -> jcfBinaryContentRepository;
        };
    }

    @Bean
    public ChannelRepository channelRepository() {
        return switch (repositoryProperties.getType()) {
            case FILE -> fileChannelRepository;
            case JCF -> jcfChannelRepository;
        };
    }

    @Bean
    public MessageRepository messageRepository() {
        return switch (repositoryProperties.getType()) {
            case FILE -> fileMessageRepository;
            case JCF -> jcfMessageRepository;
        };
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        return switch (repositoryProperties.getType()) {
            case FILE -> fileReadStatusRepository;
            case JCF -> jcfReadStatusRepository;
        };
    }

    @Bean
    public UserRepository userRepository() {
        return switch (repositoryProperties.getType()) {
            case FILE -> fileUserRepository;
            case JCF -> jcfUserRepository;
        };
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        return switch (repositoryProperties.getType()) {
            case FILE -> fileUserStatusRepository;
            case JCF -> jcfUserStatusRepository;
        };
    }
}
