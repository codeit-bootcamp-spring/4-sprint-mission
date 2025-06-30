package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Service.MessageService;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> messageList;

    public JCFMessageService() {
        messageList = new HashMap<>();
    }

    @Override
    public Message createMessage(String message) {
        Message newMessage = new Message(message);
        messageList.put(newMessage.getId(), newMessage);
        return newMessage;
    }

    @Override
    public Message searchMessage(UUID id) {
        Message findMessage = null;
        if(messageList.containsKey(id)) {
            findMessage = messageList.get(id);
        } else {
            throw new NoSuchElementException("찾지 못했어요..");
        }
        return findMessage;
    }

    @Override
    public List<Message> searchAll() {
        return messageList.values().stream().toList();
    }

    @Override
    public Message updateMessage(UUID id, String newMessage) {
        Message updatedMessage = null;
        if(newMessage != null && !newMessage.equals(updatedMessage.getContent())) {
            updatedMessage = messageList.get(id);
            updatedMessage.setContent(newMessage);
        }
        return updatedMessage;
    }

    @Override
    public void deleteMessage(UUID id) {
        if(!messageList.containsKey(id)) {
            throw new NoSuchElementException("삭제할 수 없어!");
        } else {
            messageList.remove(id);
        }

    }
}
