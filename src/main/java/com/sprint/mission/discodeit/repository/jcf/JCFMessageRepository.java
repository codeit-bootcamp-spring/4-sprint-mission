package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private static final List<Message> messages = new ArrayList<>();

    // Create
    @Override
    public void addMessageAndSave(Message message, User user, Channel channel) {
        messages.add(message);
        user.addMessage(message);
        channel.addMessage(message);
    }

    // Read
    @Override
    public List<Message> getAll() {
        return messages;
    }

    @Override
    public Message getById(UUID id) {
        return messages.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such message"));
    }

    // Update
    @Override
    public void updateContents(Message message, User user, String newContents) {
        if (!messages.contains(message)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        message.setMessageContents(newContents);
        message.setUpdatedAt(System.currentTimeMillis());
    }

    // Delete
    @Override
    public void delete(Message message, User user, Channel channel) {
        if (!messages.contains(message)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        user.removeMessageByMessageId(message.getId());
        channel.removeMessageByMessageIdFromActiveChannel(message.getId());
        messages.remove(message);
    }

    @Override
    public void deleteAll() {
        messages.forEach(message -> {
            message.getUser().removeMessageByMessageId(message.getId());
            message.getChannel().removeMessageByMessageIdFromActiveChannel(message.getId());
        });
        messages.clear();
    }
}
