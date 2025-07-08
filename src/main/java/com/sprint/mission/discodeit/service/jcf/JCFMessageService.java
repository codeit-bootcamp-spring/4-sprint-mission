package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.MessageCreateDto;
import com.sprint.mission.discodeit.dto.data.MessageResponseDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
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
    public MessageResponseDto createMessage(MessageCreateRequest messageCreateRequest) {
        UUID channelId = messageCreateRequest.channelId();
        UUID userId = createDto.userId();
        MessageCreateDto newMessage = new Message(createDto.content(), channelId, userId);
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
    public MessageResponseDto updateMessage(UUID id, MessageUpdateRequest messageUpdateRequest) {
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
