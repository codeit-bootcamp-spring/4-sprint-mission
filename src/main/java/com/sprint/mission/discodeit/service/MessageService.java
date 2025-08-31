package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(Channel channel, User user, String displayName, String content);
    Optional<Message> getMessageById(UUID id);
    List<Message> getMessages();
    List<Message> getMessageByChannel(Channel channel);
    void deleteMessage(UUID id);
}
