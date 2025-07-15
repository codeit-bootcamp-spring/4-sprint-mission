package com.codeit.discodeit.config;

import com.codeit.discodeit.repository.*;
import com.codeit.discodeit.repository.file.*;
import com.codeit.discodeit.repository.jcf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {

    private final DiscodeitRepositoryProperties properties;

    private boolean useFile() {
        return "file".equalsIgnoreCase(properties.getType());
    }

    private void ensureFileDirectoryExists() {
        String fileDirectory = properties.getFileDirectory();
        File dir = new File(fileDirectory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("⚠️ Failed to create directory: " + fileDirectory);
            } else {
                System.out.println("✅ Created data directory: " + fileDirectory);
            }
        }
    }

    @Bean
    public UserRepository userRepository() {
        if (useFile()) ensureFileDirectoryExists();
        return useFile() ?
            new FileUserRepository(properties.getFileDirectory()) :
            new JcfUserRepository();
    }

    @Bean
    public BinaryContentRepository binaryContentsRepository() {
        if (useFile()) ensureFileDirectoryExists();
        return useFile() ?
            new FileBinaryContentRepository(properties.getFileDirectory()) :
            new JcfBinaryContentRepository();
    }

    @Bean
    public ChannelRepository channelRepository() {
        if (useFile()) ensureFileDirectoryExists();
        return useFile() ?
            new FileChannelRepository(properties.getFileDirectory()) :
            new JcfChannelRepository();
    }

    @Bean
    public MessageRepository messageRepository() {
        if (useFile()) ensureFileDirectoryExists();
        return useFile() ?
            new FileMessageRepository(properties.getFileDirectory()) :
            new JcfMessageRepository();
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        if (useFile()) ensureFileDirectoryExists();
        return useFile() ?
            new FileReadStatusRepository(properties.getFileDirectory()) :
            new JcfReadStatusRepository();
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        if (useFile()) ensureFileDirectoryExists();
        return useFile() ?
            new FileUserStatusRepository(properties.getFileDirectory()) :
            new JcfUserStatusRepository();
    }
}
