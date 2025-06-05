package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try{
            // User 관련
            System.out.println("---------- USER ----------");
            UserService userService = new JCFUserService();

            // 등록
            User user1 = userService.createUser("user1");
            User user2 = userService.createUser("user2");
            System.out.println("create user");
            System.out.println("id: " + user1.getId() + ", name: " + user1.getUsername());
            System.out.println("id: " + user2.getId() + ", name: " + user2.getUsername());

            // 조회- 단건
            User searchUser = userService.findUserById(user1.getId());
            System.out.println("<search user> " + (searchUser != null ? searchUser.getUsername() : "!! null !!"));

            // 조회 - 다건
            List<User> searchUsers = userService.findAllUsers();
            System.out.println("<search all users>");
            for (User user : searchUsers) {
                System.out.println(user.getUsername());
            }

            // 수정
            userService.updateUserById(user1.getId(), "newName");
            System.out.println("<update user name>");
            // 수정 확인
            List<User> afterUpdateUser = userService.findAllUsers();
            for (User user : afterUpdateUser) {

                System.out.println(user.getUsername());
            }

            // 삭제
            userService.deleteUserById(user2.getId());
            System.out.println("<delete user name : user2>");
            // 삭제 확인
            List<User> afterDeleteUser = userService.findAllUsers();
            for (User user : afterDeleteUser) {
                System.out.println(user.getUsername());
            }


            // 채널 관련
            System.out.println("---------- CHANNEL ----------");
            ChannelService channelService = new JCFChannelService();

            // 등록
            Channel channel1 = channelService.createChannel("channel1");
            Channel channel2 = channelService.createChannel("channel2");
            System.out.println("<create channel>");
            System.out.println(channel1.getChannelName());
            System.out.println(channel2.getChannelName());

            // 단건 조회
            Channel findChannel = channelService.findChannelById(channel1.getId());
            System.out.println("<find one channel1 by id>");
            System.out.println(findChannel.getChannelName());
            // 전체 조회
            List<Channel> findAllChannels = channelService.findAllChannels();
            System.out.println("<find all channels>");
            for (Channel channel : findAllChannels) {
                System.out.println(channel.getChannelName());
            }

            // 수정
            channelService.updateChannelById(channel1.getId(), "newName");
            System.out.println("<update channel1 name by id>");
            for (Channel channel : findAllChannels) {
                System.out.println(channel.getChannelName());
            }

            // 삭제
            channelService.deleteChannelById(channel1.getId());
            System.out.println("<delete channel1 by id>");
            List<Channel> afterDeleteChannel = channelService.findAllChannels();
            for (Channel channel : afterDeleteChannel) {
                System.out.println(channel.getChannelName());
            }


            // 메세지
            System.out.println("---------- MESSAGE ----------");
            MessageService messageService = new JCFMessageService();

            // 등록
            User sender = userService.createUser("sender");
            Channel sendChannel = channelService.createChannel("sendChannel");
            Message message1 = messageService.sendMessage(sender, sendChannel, "message1");
            Message message2 = messageService.sendMessage(sender, channel2, "message2");

            System.out.println("<send message>");
            System.out.println(message1.getSender().getUsername() + " -> " + message1.getChannel().getChannelName() + ", content: " + message1.getContent());
            System.out.println(message2.getSender().getUsername() + " -> " + message2.getChannel().getChannelName() + ", content: " + message2.getContent());

            // 단건 조회
            Message findMessage = messageService.findMessage(message1.getId());
            System.out.println("<find one message1>");
            System.out.println(findMessage.getSender().getUsername() + ", content: " + findMessage.getContent());
            // 전체 조회
            List<Message> findAllMessages = messageService.findAllMessages();
            System.out.println("<find all messages>");
            for (Message message : findAllMessages) {
                System.out.println(message.getSender().getUsername() + " -> " + message.getChannel().getChannelName()+", content: " + message.getContent());
            }
            // 수정
            messageService.updateMessage(message2.getId(), "new message2");
            System.out.println("<after update message content>");
            System.out.println(message2.getContent());

            // 삭제
            messageService.deleteMessage(message1.getId());
            System.out.println("<after delete message>");
            List<Message> afterDeleteMessage = messageService.findAllMessages();
            for (Message message : afterDeleteMessage) {
                System.out.println(message.getSender().getUsername() + " -> " + message.getChannel().getChannelName() + ", content: " + message.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}