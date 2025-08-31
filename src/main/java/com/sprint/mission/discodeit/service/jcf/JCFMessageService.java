package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message sendMessage(Channel channel, User user, String displayName, String content) {
        if (user.getStatus() == UserStatus.ACTIVE) {
            data.add(new Message(user, channel, content));
            System.out.println(displayName + ": " + content);
        }
        return new Message(user, channel, content);
    }

    @Override
    public Optional<Message> getMessageById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> getMessages() {
        return data.stream()
                .toList();
    }

    @Override
    public List<Message> getMessageByChannel(Channel channel) {
        return data;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        data.removeIf(message -> message.getId().equals(messageId));
    }
}
