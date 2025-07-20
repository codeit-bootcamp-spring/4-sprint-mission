package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
        prefix="discodeit.repository",
        name="type",
        havingValue="file"
)
public class FileMessageRepository implements MessageRepository {
    private final Path filePath;

    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory}/Message.ser") String filePathStr
    ) {
        this.filePath = Paths.get(filePathStr);
        try {
            Files.createDirectories(this.filePath.getParent());
            if (Files.notExists(this.filePath)) {
                writeToFile(new HashMap<>());
            }
        } catch (IOException e) {
            // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }
    }

    @Override
    public Message save(Message message) {
        try {
            Map<UUID, Message> all = readFromFile();
            all.put(message.getId(), message);
            writeToFile(all);
            return message;
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        try {
            return Optional.ofNullable(readFromFile().get(id));
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public List<Message> findAll() {
        try {
            return new ArrayList<>(readFromFile().values());
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
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
    public void deleteById(UUID id) {
        try {
            Map<UUID, Message> all = readFromFile();
            if (all.remove(id) != null) {
                writeToFile(all);
            }
        } catch (IOException e) {
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        } catch (ClassNotFoundException e) {
            throw new FileAccessException(ErrorCode.FILE_CLASS_NOT_FOUND);
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        // 채널에 속한 메시지 파일들을 모두 삭제
        for (Message msg: findAllByChannelId(channelId)) {
            deleteById(msg.getId());
        }
    }


    /**
     * 직렬화
     */
    private void writeToFile(Map<UUID, Message> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Message file", e);
        }
    }

    /**
     * 역직렬화
     */
    private Map<UUID, Message> readFromFile() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }
}
