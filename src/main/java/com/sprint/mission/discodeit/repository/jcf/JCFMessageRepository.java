package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private static final JCFMessageRepository instance = new JCFMessageRepository();
    private JCFMessageRepository() {}
    public static JCFMessageRepository getInstance() {
        return instance;
    }

    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Optional <Message> findById(UUID messageId) {
        return Optional.ofNullable(messages.get(messageId));
    }

    @Override
    public void save(Message message) {
        messages.put(message.getMessageId(), message);
    }

    @Override
    public void deleteById(UUID messageId) {
        messages.remove(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }
}
