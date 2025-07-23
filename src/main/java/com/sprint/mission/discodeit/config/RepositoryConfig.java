package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class RepositoryConfig {

    @Value("${discodeit.repository.file-directory}")
    private Path DIRECTORY;
    @Value("${discodeit.repository.type:jcf}")
    private String type;

    @PostConstruct
    public void init() {
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(String name) {
        return DIRECTORY.resolve(name + ".ser");
    }

    @Bean
    public UserRepository userRepository() {
        if (type.equals("jcf")) {
            return new JCFUserRepository();
        } else if (type.equals("file")) {
            Path path = resolvePath("Users");
            if (Files.notExists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new FileUserRepository();
        } else throw new IllegalArgumentException("Invalid type provided");
    }

    @Bean
    public ChannelRepository channelRepository() {
        if (type.equals("jcf")) {
            return new JCFChannelRepository();
        } else if (type.equals("file")) {
            Path path = resolvePath("Channels");
            if (Files.notExists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new FileChannelRepository();
        } else throw new IllegalArgumentException("Invalid type provided");
    }

    @Bean
    public MessageRepository messageRepository() {
        if (type.equals("jcf")) {
            return new JCFMessageRepository();
        } else if (type.equals("file")) {
            Path path = resolvePath("Messages");
            if (Files.notExists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new FileMessageRepository();
        } else throw new IllegalArgumentException("Invalid type provided");
    }

    @Bean
    public BinaryContentRepository binaryContentRepository() {
        if (type.equals("jcf")) {
            return new JCFBinaryContentRepository();
        } else if (type.equals("file")) {
            Path path = resolvePath("BinaryContents");
            if (Files.notExists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new FileBinaryContentRepository();
        } else throw new IllegalArgumentException("Invalid type provided");
    }

    @Bean
    public ReadStatusRepository readStatusRepository() {
        if (type.equals("jcf")) {
            return new JCFReadStatusRepository();
        } else if (type.equals("file")) {
            Path path = resolvePath("ReadStatuses");
            if (Files.notExists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new FileReadStatusRepository();
        } else throw new IllegalArgumentException("Invalid type provided");
    }

    @Bean
    public UserStatusRepository userStatusRepository() {
        if (type.equals("jcf")) {
            return new JCFUserStatusRepository();
        } else if (type.equals("file")) {
            Path path = resolvePath("UserStatuses");
            if (Files.notExists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new FileUserStatusRepository();
        } else throw new IllegalArgumentException("Invalid type provided");
    }
}
