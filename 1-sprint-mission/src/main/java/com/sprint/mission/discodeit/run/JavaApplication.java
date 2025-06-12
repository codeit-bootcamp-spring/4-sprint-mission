package com.sprint.mission.discodeit.run;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = FileUserService.getInstance();
        ChannelService channelService = FileChannelService.getInstance();
        MessageService messageService = FileMessageService.getInstance();

        //User
        System.out.println("[=============사용자================]");

        //등록
        User user1 = userService.createUser(new User("에이미", "amy@naver.com", "010-9876-5431"));
        User user2 = userService.createUser(new User("밥", "bob@naver.com", "010-1234-5678"));
        User user3 = userService.createUser(new User("잭", "jack@naver.com", "010-2222-1111"));
        User user4 = userService.createUser(new User("다니엘","daniel@naver.com","010-2468-1357"));

        //조회 - 단건
        System.out.println("\n[유저 생성 - 1명 조회]");
        User fetchedUser = userService.getUserById(user1.getUserId());
        if (fetchedUser != null) {
            printUsers(List.of(fetchedUser));
        }

        //조회 - 다건
        System.out.println("\n[유저 생성 - 전체 조회]");
        printUsers(userService.getAllUsers());

        //수정
        userService.updateUser(user1.getUserId(),"진","jin@naver.com", "010-4455-7766");
        System.out.println("\n[사용자 수정 후]");
        User fetchedUser1 = userService.getUserById(user1.getUserId());
        if (fetchedUser1 != null) {
            printUsers(List.of(fetchedUser1));
        }

        //삭제
        userService.deleteUser(user2.getUserId());
        System.out.println("\n[삭제된 유저 제외 출력]");
        printUsers(userService.getAllUsers());

        //Channel
        System.out.println("\n[=============채널================]");

        //등록
        Channel channel1 = channelService.createChannel(new Channel("잡담방","유저 간의 잡담방"));
        Channel channel2 = channelService.createChannel(new Channel("통화방", "유저 간의 통화방"));
        Channel channel3 = channelService.createChannel(new Channel("대화방", "유저 간의 대화방"));

        //조회 - 단건
        System.out.println("\n[채널 생성 - 1개의 채널 조회]");
        Channel fetchedChannel = channelService.getChannelById(channel1.getChannelId());
        if (fetchedChannel != null) {
            printChannels(Set.of(fetchedChannel));
        }

        //조회 - 다건
        System.out.println("\n[전체 채널 탐색]");
        printChannels(channelService.getAllChannels());

        // 수정
        channelService.updateChannel(channel1.getChannelId(),"수다방","유저들이 수다떠는 방");
        System.out.println("\n[수정된 채널]");
        Channel fetchedChannel1 = channelService.getChannelById(channel1.getChannelId());
        if (fetchedChannel1 != null) {
            printChannels(Set.of(fetchedChannel1));
        }

        // 삭제
        channelService.deleteChannel(channel2.getChannelId());
        System.out.println("\n[삭제된 채널 제외 출력]");
        printChannels(channelService.getAllChannels());

        //Message
        System.out.println("\n[=============메시지================]");

        //등록
        User sender = userService.createUser(new User("럼","rum@naver.com","010-9753-8642"));
        Channel chatChannel = channelService.createChannel(new Channel("공지방", "공지가 등록될 방입니다."));

        System.out.println("\n[메시지 전송]");
        Message message1 = messageService.sendMessage(sender, chatChannel, "방을 만들었습니다. 잘 보이나요?");
        Message message2 = messageService.sendMessage(sender, chatChannel, "공지방을 만들었습니다. 한번 보시겠어요?");
        Message message3 = messageService.sendMessage(sender, chatChannel, "아무말 챌린지");
        Message message4 = messageService.sendMessage(sender, chatChannel, "이제 공지는 여기에 올라옵니다.");

        //조회 - 단건
        Message messageById = messageService.getMessageById(message1.getMessageId(), sender, chatChannel);
        System.out.println("\n[메시지 한건 조회]");
        System.out.println(messageById.getUser().getUsername() + " ->" + messageById.getChannel().getChannelname()
                + " : " + messageById.getContent());

        // 조회 - 다건
        System.out.println("\n[공지 방의 모든 메시지 조회]");
        printMessages(messageService.getMessages(sender, chatChannel));


        //수정
        messageService.updateMessage(message3.getMessageId(), "엇. 실수로 공지방에 올렸네요.");
        System.out.println("\n[수정된 메시지 조회]");
        printMessages(List.of(messageService.getMessageById(message3.getMessageId(),sender,chatChannel)));

        //삭제
        messageService.deleteMessage(message1.getMessageId());
        System.out.println("\n[메시지 삭제후]");
        printMessages(messageService.getMessages(sender,chatChannel));
    }

    private static void printUsers(List<User> users) {
        for (User user : users) {
            System.out.printf("ID: %s | 이름: %s | 이메일: %s | 전화번호: %s%n",
                    user.getUserId(), user.getUsername(), user.getEmail(), user.getPhone());
        }
    }

    private static void printChannels(Set<Channel> channels) {
        for (Channel channel : channels) {
            System.out.printf("ID: %s | 이름: %s | 설명: %s%n",
                    channel.getChannelId(), channel.getChannelname(), channel.getDescription());
        }
    }

    private static void printMessages(List<Message> messages) {
        for (Message message : messages) {
            System.out.printf("%s -> %s : %s%n",
                    message.getUser().getUsername(),
                    message.getChannel().getChannelname(),
                    message.getContent());
        }
    }
}