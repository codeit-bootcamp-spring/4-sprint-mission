package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix="discodeit.repository",
        name="type",
        havingValue="file"
)
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path filePath;

    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory}/UserStatus.ser") String filePathStr
    ) {
        this.filePath = Paths.get(filePathStr);
        try {
            Files.createDirectories(this.filePath.getParent());
            if (Files.notExists(this.filePath)) {
                writeToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize user status file", e);
        }
    }

    /**
     * 직렬화
     */
    private Map<UUID, UserStatus> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, UserStatus>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read user status file", e);
        }
    }

    /**
     * 역직렬화
     */
    private void writeToFile(Map<UUID, UserStatus> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write user status file", e);
        }
    }


    @Override
    public UserStatus save(UserStatus userStatus) {
        Map<UUID, UserStatus> all = readFromFile();
        all.put(userStatus.getId(), userStatus);
        writeToFile(all);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.of(readFromFile().get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll()
                .stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(readFromFile().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return readFromFile().containsKey(id);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return findByUserId(userId).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> all = readFromFile();
        if (all.remove(id) != null) {
            writeToFile(all);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        findByUserId(userId).ifPresent(u -> deleteById(u.getId()));
    }
}
