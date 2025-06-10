package com.sprint.mission.discodeit.run;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFchannelService;
import com.sprint.mission.discodeit.service.jcf.JCFmessageService;
import com.sprint.mission.discodeit.service.jcf.JCFuserService;

import java.util.List;
import java.util.Set;

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = new JCFuserService();
        ChannelService channelService = new JCFchannelService();
        MessageService messageService = new JCFmessageService();

        //User
        System.out.println("[=============사용자================]");

        //등록
        User user1 = userService.createUser("에이미", "amy@naver.com", "010-9876-5431");
        User user2 = userService.createUser("밥", "bob@naver.com", "010-1234-5678");
        System.out.println("유저 등록");
        System.out.println("userId = " + user1.getUserId() + ",name : " + user1.getUsername() + ",email : " + user1.getEmail() + ",phone : " + user1.getPhone());
        System.out.println("userId = " + user2.getUserId() + ",name : " + user2.getUsername() + ",email : " + user2.getEmail() + ",phone : " + user2.getPhone());

        //조회 - 단건
        User searchUser = userService.getUserById(user1.getUserId());
        System.out.println("\n[유저 탐색] " );
        System.out.println(("\n" + searchUser.getUsername()));

        //조회 - 다건
        Set<User> allUsers = userService.getAllUsers();
        System.out.println("\n[모든 유저 탐색]");
        for (User user : allUsers) {
            System.out.println(user.getUsername());
        }

        //수정
        userService.updateUser(user1.getUserId(),"진","jin@naver.com",null);
        System.out.println("\n[수정된 유저(모든 유저 출력)]");

        Set<User> afterUpdate = userService.getAllUsers();
        for (User user : afterUpdate) {

            System.out.println(user.getUsername());
            System.out.println(user.getEmail());
            System.out.println(user.getPhone());
        }

        //삭제
        userService.deleteUser(user2.getUserId());
        System.out.println("\n[삭제된 유저 제외 출력]");

        Set<User> afterDelete = userService.getAllUsers();
        for (User user : afterDelete) {

            System.out.println(user.getUsername());
        }

        //Channel
        System.out.println("\n[=============채널================]");

        //등록
        Channel channel1 = channelService.createChannel("잡담방", "유저 간의 잡담방");
        Channel channel2 = channelService.createChannel("통화방", "유저 간의 통화방");
        System.out.println("\n[채널 생성]");
        System.out.println(channel1.getChannelname());
        System.out.println(channel1.getDescription());
        System.out.println(channel2.getChannelname());
        System.out.println(channel2.getDescription());

        //조회 - 단건
        Channel findChannelById = channelService.getChannelById(channel1.getChannelId());
        System.out.println("\n[채널 탐색]");
        System.out.println(findChannelById.getChannelname());
        System.out.println(findChannelById.getDescription());

        //조회 - 다건
        Set<Channel> allChannels = channelService.getAllChannels();
        System.out.println("\n[전체 채널 탐색]");
        for (Channel channel : allChannels) {
            System.out.println(channel.getChannelname());
        }

        // 수정
        channelService.updateChannel(channel1.getChannelId(), "대화방", "대화를 위한 공간으로 이름을 변경합니다.");
        System.out.println("\n[수정된 채널]");
        Set<Channel> updatedChannel = channelService.getAllChannels();
        for (Channel channel : updatedChannel) {
            System.out.println(channel.getChannelname());
        }

        // 삭제
        channelService.deleteChannel(channel2.getChannelId());
        System.out.println("\n[삭제된 채널]");
        Set<Channel> deleteChannel = channelService.getAllChannels();
        for (Channel channel : deleteChannel) {
            System.out.println(channel.getChannelname());
        }

        //Message
        System.out.println("\n[=============메시지================]");

        //등록
        User sender = userService.createUser("잭", "jack@absc.com","010-1111-2222");
        Channel sendChannel = channelService.createChannel("수다방", "대화를 위한 공간");
        Message message1 = messageService.sendMessage(sender, sendChannel, "잘 입력되나요?");
        Message message2 = messageService.sendMessage(sender, channel1, "방이 생성되었습니다.");

        System.out.println("\n[메시지 전송]");
        System.out.println(message1.getUser().getUsername() + " -> " + message1.getChannel().getChannelname() + "내용 : " + message1.getContent());
        System.out.println(message1.getUser().getUsername() + " -> " + message2.getChannel().getChannelname() + "내용 : " + message2.getContent());

        //조회 - 단건
        Message findMessage = messageService.getMessageById(message1.getMessageId());
        System.out.println("\n[단일 메시지 조회]");
        System.out.println(findMessage.getUser().getUsername() + ", 내용 : " + findMessage.getContent());

        // 조회 - 다건
        List<Message> findAllMessages = messageService.getMessages();
        System.out.println("\n[모든 메시지 조회]");
        for (Message message : findAllMessages) {
            System.out.println(message.getUser().getUsername() + " -> " + message.getChannel().getChannelname()+", 내용 : " + message.getContent());
        }

        //수정
        messageService.updateMessage(message1.getMessageId(),"확인했습니다.");
        System.out.println("\n[메시지 수정 후]");
        System.out.println("수정된 메시지 : " + message1.getContent());

        //삭제
        messageService.deleteMessage(message1.getMessageId());
        System.out.println("\n[메시지 삭제 후]");
        List<Message> deletedMessages = messageService.getMessages();
        for (Message message : deletedMessages) {
            System.out.println(message.getUser().getUsername() + " -> " + message.getChannel().getChannelname() + ", 내용 : " + message.getContent());
        }
    }
}
