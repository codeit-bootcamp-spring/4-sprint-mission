package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
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

    private String fileName(UUID readStatusId) {
        return readStatusId + EXTENSION;
    }

    private Path resolvePath(UUID readStatusId) {
        return DIRECTORY.resolve(fileName(readStatusId));
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write read status to file", e);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            // 무시하고 다음 파일 탐색
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(readStatus -> readStatusId.equals(readStatus.getId()))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("Failed to search ReadStatus by id", e);
        }
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        try {
            List<ReadStatus> list = Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            throw new RuntimeException("Failed to read ReadStatus from file", e);
                        }
                    })
                    .filter(rs -> rs.getUserId().equals(userId)) // ✅ 이게 핵심!
                    .toList();

            return list;
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
    public boolean existsById(UUID readStatusId) {
        Path path = resolvePath(readStatusId);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID readStatusId) {
        Path path = resolvePath(readStatusId);
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
