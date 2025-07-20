package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
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
            // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }
    }

    @Override
    public User save(User user) {
        try {
            Map<UUID, User> all = readFromFile();
            all.put(user.getId(), user);
            writeToFile(all);
            return user;
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            return Optional.ofNullable(readFromFile().get(id));
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            return readFromFile().values().stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst();
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return new ArrayList<>(readFromFile().values());
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        try {
            return readFromFile().containsKey(id);
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            return readFromFile().values().stream()
                    .anyMatch(u -> u.getEmail().equals(email));
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Map<UUID, User> all = readFromFile();
            if (all.remove(id) != null) {
                writeToFile(all);
            }
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
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
    private Map<UUID, User> readFromFile() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }

}
