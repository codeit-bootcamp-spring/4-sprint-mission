package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data = new ArrayList<>();

    // 등록
    @Override
    public Message sendMessage(User sender, Channel channel, String content) {
        Message message = new Message(sender, channel, content);
        data.add(message);
        // 관계 설정
        message.setSender(sender);
        message.setChannel(channel);
        // 양방향 관계
        sender.getMessages().add(message);
        channel.getMessages().add(message);
        return message;
    }

    // 단건 조회
    /*@Override
    public Message findMessage(UUID id) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }*/
    // Stream 변경
    @Override
    public Message findMessage(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    // 다건 조회
    @Override
    public List<Message> findAllMessages() {
        return new ArrayList<>(data);
    }

    // 수정
    @Override
    public Message updateMessage(UUID id, String newContent) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                message.setContent(newContent);
                message.setUpdatedAt();
                return message;
            }
        }
        return null;
    }

    // 삭제
    @Override
    public Message deleteMessage(UUID id) {
        for (Message message : data) {
            if (message.getId().equals(id)) {
                data.remove(message);

                // 양방향 해제
                User sender = message.getSender();
                Channel channel = message.getChannel();
                sender.getMessages().remove(message);
                channel.getMessages().remove(message);
                return message;
            }
        }
        return null;
    }
}
