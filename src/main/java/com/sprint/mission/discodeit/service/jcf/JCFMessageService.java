package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService extends ErrorMessageService implements MessageService {

    private static final List<Message> messages = new ArrayList<Message>();

    // Create
    @Override
    public Optional<Message> addMessage(User user, Channel channel, String contents) {
        /*
            메시지 생성 및 검사
            1. 유저가 null이 아니면서 ACTIVE 상태여야 한다.
            2. 채널이 null이 아니면서 isActive가 true여야 한다.
            3. 채널과 유저가 각각의 리스트에 추가 되어있어야 한다.
            4. 메시지의 내용이 비어있으면 안된다.
            위의 조건을 어기면 Optional.empty() 반환
        */
        if (user != null && user.getStatus() == User.Status.ACTIVE
                && channel != null && channel.getIsActive()
                && user.getChannels().contains(channel) && channel.getUsers().contains(user)
                && contents != null && !contents.trim().isEmpty()) {
            Message message = new Message(user, channel, contents);
            messages.add(message);
            user.addMessage(message);
            channel.addMessage(message);
            return Optional.of(message);
        } else {
            printErrorMessage();
            return Optional.empty();
        }
    }

    // Read
    @Override
    public List<Message> getMessages() {
        if (messages.isEmpty()) {
            System.out.println("<메시지 리스트가 비어있습니다.>");
            return messages;
        } else {
            return messages;
        }
    }

    @Override
    public Optional<Message> getMessageById(UUID id) {
        Optional<Message> message = messages.stream().filter(m -> m.getId().equals(id)).findFirst();
        if (message.isPresent()) {
            return message;
        } else {
            printErrorMessage();
            return Optional.empty();
        }
    }

    // Update
    @Override
    public void updateMessageContentsById(UUID id, User user, String newContents) {
        Optional<Message> m = getMessageById(id);
        Message message;
        if (m.isPresent()) {
            message = m.get();
        } else {
            printErrorMessage();
            return;
        }

        /*
            메시지 업데이트 조건 검사
            1. 유저가 null이 아니며, 활동 상태에, 메시지를 보낸 유저와 같아야 한다.
            2. 새로 보낼 메시지는 null이 아니며, 공백이 아니어야 한다.
        */
        if (user != null && user.getStatus() == User.Status.ACTIVE && message.getUser() == user && newContents != null && !newContents.trim().isEmpty()) {
            message.setMessageContents(newContents);
            message.setUpdatedAt(System.currentTimeMillis());
        } else {
            printErrorMessage();
        }
    }

    // Delete
    @Override
    public void deleteMessageById(UUID id, User user, Channel channel) {
        Optional<Message> m = getMessageById(id);
        Message message;
        if (m.isPresent()) {
            message = m.get();
        } else {
            printErrorMessage();
            return;
        }
        /*
            메시지 삭제 조건 검사
            1. 유저가 null이 아니며, 활동 상태에, 메시지의 유저와 동일해야 한다.
            2. 채널은 null이 아니며, 활성화된 상태에, 메시지의 채널과 동일해야 한다.
        */
        if (user != null && user.getStatus() == User.Status.ACTIVE && message.getUser() == user
                && channel != null && channel.getIsActive() && message.getChannel() == channel) {
            user.removeMessageByMessageId(message.getId());
            channel.removeMessageByMessageId(message.getId());
            messages.remove(message);
        } else {
            printErrorMessage();
        }
    }
}


