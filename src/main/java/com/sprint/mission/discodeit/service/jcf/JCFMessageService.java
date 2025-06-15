package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private static JCFMessageService messageService;
    private final List<Message> data;

    private JCFMessageService() {
        this.data = new ArrayList<Message>();
    }
    public static JCFMessageService getInstance() {
        if (messageService == null) {
            messageService = new JCFMessageService();
        }
        return messageService;
    }

    @Override
    public Message createMessage(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public void findAllMessages() {
        data.forEach(message -> System.out.println(message.toString()));
    }

    @Override
    public Message findMessageById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 메세지가 없습니다."));
    }

    @Override
    public void updateMessage(UUID id, String updateMessage) {
        data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .ifPresent(message -> {
                    message.setText(updateMessage);
                    message.setUpdatedAt(System.currentTimeMillis());
                });

    }

    @Override
    public void deleteMessage(Message message) {
        data.remove(message);
    }
}
