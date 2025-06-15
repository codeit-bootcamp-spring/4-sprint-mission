package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class BasicMessageService {
    MessageRepository messageRepository;

    private BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    private static BasicMessageService basicMessageService;

    public static BasicMessageService getInstance(MessageRepository messageRepository) {
        if(basicMessageService == null){
            basicMessageService = new BasicMessageService(messageRepository);
        }
        return basicMessageService;
    }

    public Message sendMessage(Path dir, Message message) {
        return messageRepository.sendMessage(dir, message);
    }
    public List<Message> loadAllMessages(Path dir) {
        return messageRepository.loadAllMessages(dir);
    }
    public Optional<Message> loadMessageById(Path dir, UUID id) {
        return messageRepository.loadMessageById(dir, id);
    }
    public void deleteMessage(Path dir, Message message) {
        messageRepository.deleteMessage(dir, message);
    }
}
