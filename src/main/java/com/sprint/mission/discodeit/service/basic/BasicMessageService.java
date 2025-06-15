package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;
import com.sprint.mission.discodeit.service.utility.MessageValidator;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Create
    @Override
    public Message createMessage(String contents, User user, Channel channel) {
        if(!MessageValidator.detectMessageCreate(contents,user,channel)){
            throw new RuntimeException("Invalid contents or user or channel");
        }
        Message message = new Message(contents, user, channel);
        messageRepository.addMessageAndSave(message, user, channel);
        return message;
    }

    // Read
    @Override
    public List<Message> getMessages() {
        return messageRepository.getAll();
    }

    @Override
    public Message getMessageById(UUID id) {
        return messageRepository.getById(id);
    }

    // Update
    @Override
    public void updateMessageContentsByMessage(Message message, User user, String newContents) {
        if (!MessageValidator.detectMessageUpdate(message, user, newContents)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        messageRepository.updateContents(message, user, newContents);
    }

    // Delete
    @Override
    public void deleteMessageByMessage(Message message, User user, Channel channel) {
        if (!MessageValidator.detectMessageDelete(message, user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        messageRepository.delete(message, user, channel);
    }

    @Override
    public void deleteAllMessages() {
        messageRepository.deleteAll();
    }
}
