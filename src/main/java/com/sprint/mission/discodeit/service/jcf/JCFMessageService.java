package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;
import com.sprint.mission.discodeit.service.utility.MessageValidator;

import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private static final JCFMessageRepository jcfMessageRepository = new JCFMessageRepository();

    // Create
    @Override
    public Message createMessage(String contents, User user, Channel channel) {
        if(!MessageValidator.detectMessageCreate(contents,user,channel)){
            throw new RuntimeException("Invalid contents or user or channel");
        }
        Message message = new Message(contents, user, channel);
        jcfMessageRepository.addMessageAndSave(message, user, channel);
        return message;
    }

    // Read
    @Override
    public List<Message> getMessages() {
        return jcfMessageRepository.getAll();
    }

    @Override
    public Message getMessageById(UUID id) {
        return jcfMessageRepository.getById(id);
    }

    // Update
    @Override
    public void updateMessageContentsByMessage(Message message, User user, String newContents) {
        if (!MessageValidator.detectMessageUpdate(message, user, newContents)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfMessageRepository.updateContents(message, user, newContents);
    }

    // Delete
    @Override
    public void deleteMessageByMessage(Message message, User user, Channel channel) {
        if (!MessageValidator.detectMessageDelete(message, user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfMessageRepository.delete(message, user, channel);
    }

    @Override
    public void deleteAllMessages() {
        jcfMessageRepository.deleteAll();
    }
}
