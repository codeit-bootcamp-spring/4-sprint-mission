package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileReadStatusRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory for ReadStatus", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public UUID save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ReadStatus", e);
        }
        return readStatus.getId();
    }

    @Override
    public List<ReadStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("Failed to read ReadStatus file: " + path.getFileName(), e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list ReadStatus directory", e);
        }
    }

    @Override
    public ReadStatus findById(UUID Id) {
        return findAll().stream()
                .filter(rs -> rs.getId().equals(Id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("ReadStatus with id " + Id + " not found"));
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<ReadStatus> toDelete = findByUserId(userId);
        toDelete.forEach(rs -> deleteById(rs.getId()));
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        List<ReadStatus> toDelete = findByChannelId(channelId);
        toDelete.forEach(rs -> deleteById(rs.getId()));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return !findByUserId(userId).isEmpty();
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete ReadStatus file with id: " + id, e);
        }
    }
}
