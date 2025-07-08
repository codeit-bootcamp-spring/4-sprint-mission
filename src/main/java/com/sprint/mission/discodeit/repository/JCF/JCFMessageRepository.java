package com.sprint.mission.discodeit.repository.JCF;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messageData;

    public JCFMessageRepository() {
        this.messageData = new HashMap<>();
    }

    @Override
    public Message save(Message channel) {
        this.messageData.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Message> findId(UUID id) {
        return Optional.ofNullable(this.messageData.get(id));
    }

    @Override
    public List<Message> findAll(UUID id) {
        return this.messageData.values().stream()
                .filter(message -> message.getChannelId().equals(id)).toList();
    }

    @Override
    public boolean existId(UUID id) {
        return this.messageData.containsKey(id);
    }

    @Override
    public void deleteId(UUID id) {
        this.messageData.remove(id);
    }
}