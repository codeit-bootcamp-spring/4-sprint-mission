package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    HashMap<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMessage(Message message, User user, Channel channel) {
        data.put(message.getId(), message);

        //추가
        user.addMessage(message);
        channel.addMessage(message);
        channel.addUser(user);
        message.addUser(user);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateMessage(UUID id, String newMessage) {
        Message message = data.get(id);
        if (message != null) {
            message.updateContent(newMessage);
        }
    }

    @Override
    public boolean deleteMessage(UUID id) {
        if (!data.containsKey(id)) {
            System.out.println("삭제할 메세지가 존재하지 않습니다");
            return false;
        }
        data.remove(id);
        return true;
    }
}