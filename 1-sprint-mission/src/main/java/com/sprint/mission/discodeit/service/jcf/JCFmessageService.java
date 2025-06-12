package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFmessageService implements MessageService {

    private final List<Message> data;
    private static final JCFmessageService instance = new JCFmessageService();

    private JCFmessageService() {
        this.data = new ArrayList<>();
    }

    public static JCFmessageService getInstance() {
        return instance;
    }

    @Override
    public Message sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        data.add(message);
        channel.addMessage(message);
        message.setUser(user);
        message.setChannel(channel);

        return message;
    }

    @Override
    public List<Message> getMessages(User user, Channel channel) {
        return new ArrayList<>(data);
    }

    @Override
    public Message getMessageById(String messageId, User user, Channel channel) {
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

    @Override
    public List<Message> deleteMessagesByChannelId(String channelId) {
        List<Message> deletedmessages = new ArrayList<>();
        for (Message message : data) {
            if (message.getChannel().getChannelId().equals(channelId)) {
                deletedmessages.add(message);
            }
        }

        data.removeIf(message -> message.getChannel().getChannelId().equals(channelId));

        return deletedmessages;
    }
}

