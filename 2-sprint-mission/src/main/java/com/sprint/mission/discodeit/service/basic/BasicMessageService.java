package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message addMessage(String content, User user, Channel channel) {
        Message message = new Message(content, user, channel);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Optional<Message> findMessageById(UUID messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> findMessagesByContentContains(String MessageName) {
        return messageRepository.findMessagesByContentContains(MessageName);
    }

    @Override
    public void updateMessage(UUID MessageId, String updatedText) {
        Message message = isExistMessage(MessageId);
        message.setContents(updatedText);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

    @Override
    public void deleteAllMessagesByUser(User user) {
        User findUser = isExistUser(user);
        List<Message> userMessages = findUser.getMessages();
        userMessages.forEach(message -> messageRepository.delete(message));
        findUser.removeAllMessages();
    }

    public Message isExistMessage(Message message) {
        return messageRepository.findById(message.getMessageId())
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }
    public Message isExistMessage(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }
    public User isExistUser(User user) {
        return userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}
