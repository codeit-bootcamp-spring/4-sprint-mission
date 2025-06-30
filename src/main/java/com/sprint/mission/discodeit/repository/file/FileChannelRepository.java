package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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
public class FileChannelRepository implements ChannelRepository {
    private final Path filePath;

    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory}/Channel.ser") String filePathStr
    ) {
        this.filePath = Paths.get(filePathStr);
        try {
            Files.createDirectories(this.filePath.getParent());
            if (Files.notExists(this.filePath)) {
                // 빈 맵으로 초기 파일 생성
                writeToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize Channel file", e);
        }
    }

    /**
     * 직렬화
     */
    private void writeToFile(Map<UUID, Channel> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(filePath))) {
            oos.writeObject(map);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write Channel file", e);
        }
    }
    /**
     * 역직렬화
     */
    private Map<UUID, Channel> readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(filePath))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (EOFException eof) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read Channel file", e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> all = readFromFile();
        all.put(channel.getId(), channel);
        writeToFile(all);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(readFromFile().get(id));
    }

    @Override
    public List<Channel> findAllByChannelType(ChannelType channelType) {
        return findAll()
                .stream()
                .filter(c -> c.getType() == channelType)
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(readFromFile().values());
    }

    @Override
    public boolean existsById(UUID id) {
        return readFromFile().containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, Channel> all = readFromFile();
        if (all.remove(id) != null) {
            writeToFile(all);
        }
    }
}
