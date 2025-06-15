package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.nio.file.Path;
import java.util.*;

public class JCFMessageRepository implements MessageRepository {

    private static JCFMessageRepository jcfMessageRepository;
    private final List<Message> data;

    private JCFMessageRepository() { this.data = new ArrayList<Message>(); }
    public static JCFMessageRepository getInstance() {
        if (jcfMessageRepository == null) {
            jcfMessageRepository = new JCFMessageRepository();
        }
        return jcfMessageRepository;
    }

    @Override
    public Message sendMessage(Path dir, Message message) {
        data.add(message);
        return message;
    }

    @Override
    public List<Message> loadAllMessages(Path dir) {
        return data.stream()
                .toList();
    }

    @Override
    public Optional<Message> loadMessageById(Path dir, UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteMessage(Path dir, Message message) {
        data.remove(message);
    }
}
