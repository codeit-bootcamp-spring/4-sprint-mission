package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileMessageRepository implements MessageRepository {
    private static final String DATA_FILE = "data/message.ser";
    private final Map<UUID, Message> messageCache = new ConcurrentHashMap<>();
    private final String fileDirectory;

    public FileMessageRepository(String fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadAllMessagesToCache();
    }

    public void saveAllMessageToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<Message> messages = new ArrayList<>(messageCache.values());
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("저장 오류: " + e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAllMessagesToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("데이터 파일이 존재하지 않습니다: " + DATA_FILE);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Message> messages = (List<Message>) ois.readObject();
            messages.forEach(
                    message -> {
                        messageCache.put(message.getId(), message);
                    });
        } catch (IOException e) {
            System.err.println("파일 입출력 오류 발생: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("클래스를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public Message save(Message message) {
        messageCache.put(message.getId(), message);
        saveAllMessageToFile();
        return message;
    }

    @Override
    public List<Message> findAll() {
        return messageCache.values().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .toList();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = messageCache.get(id);
        if (message == null || message.getState() == MessageState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(message);
    }

    @Override
    public void delete(UUID id) {
        Message cached = messageCache.get(id);
        if (cached != null && cached.getState() != MessageState.DELETED) {
            cached.setToDeleted();
            saveAllMessageToFile();
        }
    }

    @Override
    public List<Message> findMessagesByContentContains(String MessageName) {
        return messageCache.values().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .filter(message -> message.getContents().contains(MessageName))
                .toList();
    }
}
