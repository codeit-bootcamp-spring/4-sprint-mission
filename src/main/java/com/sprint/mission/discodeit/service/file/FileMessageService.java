package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;
import com.sprint.mission.discodeit.service.utility.MessageValidator;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private static final FileMessageRepository fileMessageRepository = new FileMessageRepository();


    @Override
    public Message createMessage(String contents, User user, Channel channel) {
        if (!MessageValidator.detectMessageCreate(contents, user, channel)) {
            throw new RuntimeException("<실패: 메시지 생성에 실패하였습니다.>");
        }
        Message message = new Message(contents, user, channel);
        fileMessageRepository.addMessageAndSave(message, user, channel);
        return message;
    }

    // Read
    @Override
    public List<Message> getMessages() {
        return fileMessageRepository.getAll();
    }

    @Override
    public Message getMessageById(UUID id) {
        return fileMessageRepository.getById(id);
    }

    // Update
    @Override
    public void updateMessageContentsByMessage(Message message, User user, String newContents) {
        if (!MessageValidator.detectMessageUpdate(message, user, newContents)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileMessageRepository.updateContents(message, user, newContents);
    }

    // Delete
    @Override
    public void deleteMessageByMessage(Message message, User user, Channel channel) {
        if (!MessageValidator.detectMessageDelete(message, user, channel)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        fileMessageRepository.delete(message, user, channel);
    }

    @Override
    public void deleteAllMessages() {
        fileMessageRepository.deleteAll();
    }

}
