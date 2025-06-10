package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message addMessage(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public List<Message> getMessages() {
        return data.stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        return data.stream()
                .filter(message -> message.getMessageId().equals(messageId))
                .filter(message -> message.getState() != MessageState.DELETED)
                .findFirst();
    }

    @Override
    public List<Message> findMessagesByContentContains(String MessageContents) {
        return data.stream()
                .filter(message -> message.getContent().contains(MessageContents))
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void updateMessage(UUID messageId,String updatedText) {
        data.stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .filter(message -> message.getMessageId().equals(messageId))
                .findFirst()
                .ifPresent(message -> {
                    message.setContents(updatedText);
                    message.setUpdatedAt();
                });
    }

    @Override
    public void deleteMessage(Message message) {
        if(message.getState() != MessageState.DELETED) {
            message.deletedMessageState();
            data.remove(message);
        }
    }

    @Override
    public void deleteAllMessagesByUser(User user) {
        user.getMessages().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .forEach(Message::deletedMessageState);
        user.removeAllMessages();
    }

}
