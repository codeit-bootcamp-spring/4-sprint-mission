package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.file.FileIOHelper;

import java.nio.file.Path;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private static FileMessageRepository instance;
    private final Map<UUID, Message> data;
    private final Path filePath;

    private FileMessageRepository(Path filePath) {
        this.filePath = filePath;
        this.data = FileIOHelper.loadMap(filePath);
    }

    public void saveAllMessages() {
        FileIOHelper.saveMap(filePath, data);
    }

    public static FileMessageRepository getInstance(Path filePath) {
        if (instance == null) {
            instance = new FileMessageRepository(filePath);
        }
        return instance;
    }

    // 이후 호출 시 filePath 없이 사용
    public static FileMessageRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FileMessageRepository가 아직 초기화되지 않았습니다. getInstance(Path filePath)를 먼저 호출하세요.");
        }
        return instance;
    }

    @Override
    public Message save(Message message) {
        data.put(message.getMessageId(), message);
        saveAllMessages();
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
        saveAllMessages();
    }

    @Override
    public Message findById(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> findByBody(String messageBody) {
        List<Message> result = new ArrayList<>();
        for (Message message : data.values()) {
            if (message.getMessageBody() != null && message.getMessageBody().contains(messageBody)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean isContains(UUID messageId) {
        return data.containsKey(messageId);
    }
}
