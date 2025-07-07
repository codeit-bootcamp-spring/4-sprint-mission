package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserRepository implements UserRepository {
    private final Path filePath;

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory}/User.ser") String filePathStr
    ) {
        this.filePath = Paths.get(filePathStr);
        try {
            Files.createDirectories(this.filePath.getParent());
            if (Files.notExists(this.filePath)) {
                // 빈 맵으로 초기 파일 생성
                writeToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize user file", e);
        }
    }

    @Override
    public User save(User user) {
        Map<UUID, User> all = readFromFile();
        all.put(user.getId(), user);
        writeToFile(all);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(readFromFile().get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return readFromFile().values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(readFromFile().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return readFromFile().containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return readFromFile().values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, User> all = readFromFile();
        if (all.remove(id) != null) {
            writeToFile(all);
        }
    }


    /**
     * 직렬화
     */
    private void writeToFile(Map<UUID, User> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write user file", e);
        }
    }

    /**
     * 역직렬화
     */
    private Map<UUID, User> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read user file", e);
        }
    }

}
