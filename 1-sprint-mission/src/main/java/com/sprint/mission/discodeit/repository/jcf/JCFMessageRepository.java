package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

public class JCFMessageRepository  implements MessageRepository {

    private final List<Message> data = new ArrayList<>();

    @Override
    public Message save(Message message) {
        data.add(message);
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Message findById(String messageId, User user, Channel channel) {
        for (Message message : data) {
            if (message.getMessageId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public Message delete(String messageId) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getMessageId().equals(messageId)) {
                return data.remove(i);
            }
        }
        return null;
    }

    @Override
    public List<Message> deleteByChannelId(String channelId) {
        List<Message> delete = new ArrayList<>();
        for (int i = data.size() - 1; i >= 0; i--) {
            Message message = data.get(i);
            if (message.getChannel().getChannelId().equals(channelId)) {
                delete.add(message);
                data.remove(i);
            }
        }
        return delete;
    }

    @Override
    public List<Message> findByUserAndChannel(User user, Channel channel) {
        return findAll().stream()
                .filter(m -> (user == null || m.getUser().getUserId().equals(user.getUserId())) &&
                        (channel == null || m.getChannel().getChannelId().equals(channel.getChannelId())))
                .toList();

    }
}