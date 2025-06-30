package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

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
    public List<Message> findAll() {
        return this.messageData.values().stream().toList();
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