package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/***********************************
 * 메세지 서비스 인터페이스 구현체
 * CRUD 실행
 * 2025.05.30 김민수
 ***********************************/
public class JCFMessageService implements MessageService {

    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }


    // 메시지 생성
    @Override
    public Message createMessage(String content, User user, Channel channel) {
        Message message = new Message(content,user,channel);
        if (user.isActive() && channel.isActive()) {
            data.add(message);
            channel.addMessage(message);
            user.addMessage(message);
            return message;
        } else {
            return message;
        }
    }


    // 모든 메시지 확인
    @Override
    public List<Message> getMessages() {
        return data;
    }

    // 특정 ID를 가진 메시지 확인
    @Override
    public Optional<Message> getMessagesById(UUID messageId) {
        return data.stream()
                .filter(msg -> msg.getId().equals(messageId))
                .findFirst();
    }

    // 메시지 내용 수정
    // 현재는 내용 1개만 수정 가능
    @Override
    public void updateMessage(UUID messageId, String content) {
        Optional<Message> tmp = data.stream()
                .filter(msg -> msg.getId().equals(messageId))
                .findFirst();
        if (tmp.isPresent()) {
            tmp.get().setContent(content);
            tmp.get().setUpdatedAt(System.currentTimeMillis());
        }
    }

    // 메시지 삭제
    public void removeMessage(Message message) {
        if(data.contains(message)){
            data.remove(message);
            message.getUser().removeMessage(message);
            message.getChannel().removeMessage(message);
        }
    }
}
