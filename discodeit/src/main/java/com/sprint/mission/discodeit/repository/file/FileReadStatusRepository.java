package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileReadStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory for ReadStatus files", e);
            }
        }
    }

    private String fileName(UUID userId, UUID channelId) {
        return userId + "-" + channelId + EXTENSION;
    }

    private Path resolvePath(UUID userId, UUID channelId) {
        return DIRECTORY.resolve(fileName(userId, channelId));
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getUserId(), readStatus.getChannelId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write read status to file", e);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        Path path = resolvePath(userId, channelId);
        if (!Files.exists(path)) return Optional.empty();

        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            return Optional.of((ReadStatus) ois.readObject());
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Failed to read ReadStatus from file", e);
        }
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        try {
            List<ReadStatus> findAllUser = Files.list(DIRECTORY)
                    .filter(path -> path.getFileName().toString().startsWith(userId.toString() + "-"))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            throw new RuntimeException("Failed to read ReadStatus from file", e);
                        }
                    })
                    .toList();

            return findAllUser;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read ReadStatus from file", e);
        }
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        try {
            List<ReadStatus> findAllChannel = Files.list(DIRECTORY)
                    .filter(path -> path.getFileName().toString().endsWith("-" + channelId + EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            throw new RuntimeException("Failed to read ReadStatus from file", e);
                        }
                    })
                    .toList();

            return findAllChannel;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read ReadStatus from file", e);
        }
    }

    @Override
    public boolean existsById(UUID userId, UUID channelId) {
        Path path = resolvePath(userId, channelId);
        return Files.exists(path);
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        Path path = resolvePath(userId, channelId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.getFileName().toString().startsWith(userId.toString() + "-"))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete ReadStatus from file", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete ReadStatus from file", e);
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.getFileName().toString().endsWith("-" + channelId + EXTENSION))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete ReadStatus from file", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete ReadStatus from file", e);
        }
    }
}
