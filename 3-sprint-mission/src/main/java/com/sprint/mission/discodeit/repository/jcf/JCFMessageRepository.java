package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new ConcurrentHashMap<>();

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return data.values().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = data.get(id);
        if (message == null || message.getState() == MessageState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(message);
    }

    @Override
    public void delete(UUID id) {
        Message existing = data.get(id);
        if (existing != null && existing.getState() != MessageState.DELETED) {
            existing.deletedMessageState();
        }
    }

    @Override
    public List<Message> findMessagesByContentContains(String MessageName) {
        return data.values().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .filter(message -> message.getContents().contains(MessageName))
                .collect(Collectors.toList());
    }
}
