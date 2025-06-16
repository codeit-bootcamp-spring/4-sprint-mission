package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.ArrayList;

public class JCFMessageRepository implements MessageRepository {

    private static JCFMessageRepository instance = new JCFMessageRepository();
    private final ArrayList<Message> data;

    public JCFMessageRepository() {
        data = new ArrayList<>();
    }

    public static JCFMessageRepository getInstance() {
        return instance;
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return null;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s' 비활성 상태라 메세지를 작성할 수 없습니다.", user.getUserName());
            return null;
        }
        System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n",
                user.getId(), channel.getId(), contents);
        Message newMessage = new Message(user, channel, contents);
        data.add(newMessage);
        newMessage.registerMessageToUserAndChannel(user, channel);

        return newMessage;
    }

    @Override
    public void deleteMessage(User user, Message message) {
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s' 비활성 상태라 메세지를 삭제할 수 없습니다.", user.getUserName());
            return;
        }
        if (user.getId().equals(message.getUser().getId())) {
            System.out.printf("메세지를 삭제합니다. User: %s, MessageId: %s, Contents: %s%n",
                    user.getUserName(), message.getId(), message.getMessageContents());
        } else {
            System.out.printf("'%s' 은 '%s' 메시지 주인이 아닙니다%n",
                    user.getUserName(), message.getId());
            return;
        }
        message.getChannel().getMessages().remove(message); // 채널에서 메세지 삭제
        user.removeMessage(message);
        data.remove(message);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents) {
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        if(user.getStatus().equals(UserStatus.DEACTIVE)) {
            System.out.printf("'%s' 비활성 상태라 메세지를 업데이트할 수 없습니다.", user.getUserName());
            return;
        }
        if(!user.equals(message.getUser())) {
            System.out.printf("'%s'는 '%s' 메시지의 주인이 아닙니다. 따라서 해당 메시지를 '%s'로 바꾸는 것은 불가능합니다.", user.getUserName(), message.getMessageContents(), newContents);
            return;
        }

        System.out.println("메세지를 업데이트 합니다.");
        System.out.printf("이전 메세지: %s%n", message.getMessageContents());
        System.out.printf("현재 메세지: %s%n", newContents);
        message.updateMessageContent(newContents);
    }

    public void printAllMessage() {
        System.out.printf("전체 메시지를 출력합니다. 메세지 수: %d%n", data.stream()
                .filter(message -> message.getUser().getStatus().equals(UserStatus.ACTIVE))
                .toList().size());
        data
                .stream().filter(message -> message.getUser().getStatus().equals(UserStatus.ACTIVE))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n", message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public ArrayList<Message> getMessages() {
        return data;
    }
}
