package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private static final FileMessageService instance = new FileMessageService();
    private final MessageRepository messageRepository = FileMessageRepository.getInstance();

    private FileMessageService() {
    }

    public static FileMessageService getInstance() {
        return instance;
    }

    @Override
    public Message sendMessage(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        user.getMessages().add(message);
        channel.getMessages().add(message);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(User user, Channel channel) {
        return messageRepository.findByUserAndChannel(user, channel);
    }

    @Override
    public Message getMessageById(String messageId, User user, Channel channel) {
        return messageRepository.findById(messageId, user, channel);
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