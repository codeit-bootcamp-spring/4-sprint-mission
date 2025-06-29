package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", UserStatus.class.getSimpleName());
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create UserStatus directory", e);
        }
    }

    private Path resolvePath(UUID userId) {
        return DIRECTORY.resolve(userId.toString() + EXTENSION);
    }

    @Override
    public void save(UserStatus status) {
        Path path = resolvePath(status.getUserId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(status);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save UserStatus", e);
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        Path path = resolvePath(userId);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.of((UserStatus) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read UserStatus for userId: " + userId, e);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            Files.deleteIfExists(resolvePath(userId));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete UserStatus for userId: " + userId, e);
        }
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return Files.exists(resolvePath(userId));
    }

    @Override
    public List<UserStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to load UserStatus from file: " + path, e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list UserStatus files", e);
        }
    }
}
