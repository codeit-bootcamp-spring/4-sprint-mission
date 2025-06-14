package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findAll();
    List<Message> findByUserAndChannel(User user, Channel channel);
    Message findById(String messageId, User user, Channel channel);
    Message delete(String messageId);
    List<Message> deleteByChannelId(String channelId);
}