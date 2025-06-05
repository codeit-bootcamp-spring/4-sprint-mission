package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFmessageService implements MessageService {

    private final List<Message> data;

    public JCFmessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        data.add(message);
        message.setUser(user);
        message.setChannel(channel);

        return message;
    }

    @Override
    public List<Message> getMessages() {
        return new ArrayList<>(data);
    }

    @Override
    public Message getMessageById(String messageId) {
        for (Message message : data) {
            if (message.getMessageId().equals(messageId)) {
                return message;
            }
        } return null;
    }

    @Override
    public Message updateMessage(String messageId, String newContent) {
        for (Message message : data) {
            if (message.getMessageId().equals(messageId)) {
                message.setContent(newContent);
                message.setUpdatedAt(System.currentTimeMillis());

                return message;
            }
        }
        return null;
    }

    @Override
    public Message deleteMessage(String messageId) {
        for (Message message : data) {
            if (message.getMessageId().equals(messageId)) {
                data.remove(message);
                return message;
            }
        }
        return null;
    }
}

