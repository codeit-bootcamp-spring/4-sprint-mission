package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.entity.User;
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
    public Message addMessage(String content, User user, Channel channel) {
        Message message = new Message(content, user, channel);
        data.add(message);
        return message;
    }

    @Override
    public List<Message> findAllMessage() {
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
    public List<Message> findMessagesByContentContains(String content) {
        return data.stream()
                .filter(message -> message.getContent().contains(content))
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
