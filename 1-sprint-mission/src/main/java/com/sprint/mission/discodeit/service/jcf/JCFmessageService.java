package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFmessageService implements MessageService {

    private static final JCFmessageService instance = new JCFmessageService();
    private final MessageRepository messageRepository;

    private JCFmessageService() {
        this.messageRepository = new JCFMessageRepository();
    }

    public static JCFmessageService getInstance() {
        return instance;
    }

    @Override
    public Message sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        channel.addMessage(message);
        message.setUser(user);
        message.setChannel(channel);

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(User user, Channel channel) {
        return messageRepository.findAll().stream()
                .filter(message -> (user == null || message.getUser().getUserId().equals(user.getUserId())) &&
                        (channel == null || message.getChannel().getChannelId().equals(channel.getChannelId())))
                .collect(Collectors.toList());
    }

    @Override
    public Message getMessageById(String messageId, User user, Channel channel) {
        Message message = messageRepository.findById(messageId, user, channel );

        if (message == null) {
            return null;
        } else {
            boolean userMatches = user == null || message.getUser().getUserId().equals(user.getUserId());
            boolean channelMatches = channel == null || message.getChannel().getChannelId().equals(channel.getChannelId());

            return (userMatches && channelMatches) ? message : null;
        }
    }

    @Override
    public Message updateMessage(String messageId, String newContent) {

        Message message = messageRepository.findById(messageId, null, null);
        if (message != null) {
            message.setContent(newContent);
            message.setUpdatedAt(System.currentTimeMillis());

            messageRepository.delete(messageId);
            return messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Message not found");
        }
    }

    @Override
    public Message deleteMessage(String messageId) {
        return messageRepository.delete(messageId);
    }

    @Override
    public List<Message> deleteMessagesByChannelId(String channelId) {
        return messageRepository.deleteByChannelId(channelId);
    }
}

