package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {
    private static JCFMessageRepository instance;
    private final Map<UUID, Message> data = new HashMap<>();

    private JCFMessageRepository() {}

    public static JCFMessageRepository getInstance() {
        if (instance == null) {
            instance = new JCFMessageRepository();
        }
        return instance;
    }

    @Override
    public Message save(Message message) {
        data.put(message.getMessageId(), message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }

    @Override
    public Message findById(UUID messageId) {
        return data.get(messageId);
    }

    @Override
    public List<Message> findByBody(String messageBody) {
        return data.values().stream()
                .filter(message -> message.getMessageBody().contains(messageBody))
                .collect(Collectors.toList());
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
