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
import java.util.stream.Stream;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path DIRECTORY;
    private final String EXTENSION;

    public FileReadStatusRepository(String fileDirectory) {
        this.EXTENSION = ".ser";
        this.DIRECTORY = Paths.get(fileDirectory, ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Path path = resolvePath(readStatus.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(readStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatusNullable = null;
        Path path = resolvePath(id);

        if (Files.notExists(path)) {
            throw new RuntimeException(this.getClass().getSimpleName() + ".findById 실패: No such file or directory");
        }

        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            readStatusNullable = (ReadStatus) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(readStatusNullable);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return findAllById(userId);
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return findAllById(channelId);
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ReadStatus> findAllById (UUID id) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (ReadStatus) ois.readObject();
                        } catch (ClassNotFoundException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(readStatus -> readStatus.getUserId().equals(id))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
