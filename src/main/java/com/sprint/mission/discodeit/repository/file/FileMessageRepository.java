package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
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
            throw new RuntimeException("Cannot initialize Message file", e);
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
    private Map<UUID, Message> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read Message file", e);
        }
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> all = readFromFile();
        all.put(message.getId(), message);
        writeToFile(all);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(readFromFile().get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(readFromFile().values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return findAll().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return readFromFile().containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Message> all = readFromFile();
        if (all.remove(id) != null) {
            writeToFile(all);
        }
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        // 채널에 속한 메시지 파일들을 모두 삭제
        findAllByChannelId(channelId)
                .stream()
                .map(Message::getId)
                .forEach(this::deleteById);
    }
}
