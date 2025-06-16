package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.factory.RepositoryFactory;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message createMessage(User user, Channel channel, String contents) {
        return messageRepository.createMessage(user, channel, contents);
    }

    @Override
    public void deleteMessage(User user, Message message) {
        messageRepository.deleteMessage(user, message);
    }

    @Override
    public void updateMessage(User user, Message message, String newContents) {
        messageRepository.updateMessage(user, message, newContents);
    }

    @Override
    public void printMessagesByChannel(Channel channel) {
        if(channel == null) {
            System.out.println("채널이 null 입니다.");
            return;
        }
        List<Message> data = messageRepository.getMessages();
        System.out.printf("%s 채널의 메세지를 조회합니다. 메시지 수: %d%n",
                channel.getChannelName(), channel.getMessages().size());

        data.stream()
                .filter(message -> message.getChannel().equals(channel))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n",
                        message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public void printMessage(Message message) {
        if(message == null) {
            System.out.println("메세지가 null 입니다.");
            return;
        }
        System.out.printf("메세지 ID: %s 의 정보를 출력합니다.%n", message.getId());
        System.out.println(message.toString());
    }

    @Override
    public void printAllMessage() {
        List<Message> data = messageRepository.getMessages();
        System.out.printf("전체 메시지를 출력합니다. 메세지 수: %d%n", data.size());
        
        data.stream().filter(message -> message.getUser().getStatus().equals(UserStatus.ACTIVE))
                .forEach(message -> System.out.printf("작성자: %s, 내용: %s%n", message.getUser().getUserName(), message.getMessageContents()));
    }

    @Override
    public void printAllMessageByUser(User user) {
        // 이렇게 하면 최신 저장된 메시지가 출력되지는 않을 것 같은데
        System.out.printf("%s이 작성한 모든 메세지를 출력합니다. 메시지 수: %d%n",
                user.getUserName(), user.getMessages().size());
        user.getMessages()
                .forEach(message -> System.out.printf("내용: %s%n", message.getMessageContents()));
    }
}
