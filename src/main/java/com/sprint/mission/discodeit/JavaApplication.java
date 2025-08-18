package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        /*

        //아이디
        //아이디 등록
        User user1 = userService.createUser(new User("강은혁"));
        User user2 = userService.createUser(new User("자바"));

        //아이디 단건 조회
        System.out.println("아이디 단건 조회 : " + userService.getUser(user1.getId()).getUsername());

        //아이디 다건 조회
        for(User u : userService.getUsers()) {
            System.out.println("아이디 다건 조회 : " + u.getUsername());
        }

        //아이디 수정
        userService.updateUser(user1.getId(), "Hello");
        //수정된 아이디 조회
        System.out.println("수정 된 아이디 조회 : " + userService.getUser(user1.getId()).getUsername());

        //아이디 삭제
        userService.deleteUser(user1.getId());
        //아이디 삭제 후 조회
        System.out.println("삭제 후 조회 : " + userService.getUser(user1.getId())== null ? "존재함" : "존재하지않음");


        //채널
        Channel channel1 = new Channel("Discodeit");
        Channel channel2 = new Channel("Hello");
        //채널 생성
        channelService.createChannel(channel1);
        channelService.createChannel(channel2);

        //채널 단건 조회
        System.out.println("채널 단건 조회 : " + channelService.getChannel(channel1.getId()).getChannel());

        //채널 전체조회
        for(Channel ch : channelService.getChannels()) {
            System.out.println("채널 다건 조회 : " + ch.getChannel());
        }

        //채널 수정
        channelService.updateChannel(channel1.getId(), "Discord");
        System.out.println("채널의 수정 된 이름 : " + channelService.getChannel(channel1.getId()).getChannel());

        //채널 삭제
        channelService.deleteChannel(channelService.getChannel(channel1.getId()).getId());

        //채널 삭제 후 조회
        System.out.println("삭제 된 채널 : " + channelService.getChannel(channel1.getId()) == null ? "존재함" : "존재하지않음");
        */

        //메세지
        User user = new User("EH");
        Channel ch = new Channel("Discodeit");
        Channel ch2 = new Channel("chtest");

        Message message1 = new Message("Hello Discord!", user, ch);
        Message message2 = new Message("test22", user, ch2);


        //메세지 전송
        System.out.println("메세지 : " + messageService.createMessage(message1, user, ch).getContent());
        messageService.createMessage(message2, user, ch);

        //단건 조회
        System.out.println("메세지 단건 조회 : " + messageService.getMessage(message1.getId()).getContent());


        //전체 조회
        for(Message m : messageService.getMessages()){
            System.out.println("메세지 다건 조회 : " + m.getContent());
        }

        //수정
        messageService.updateMessage(message1.getId(), "헬로 디스코드!");

        //수정 후 조회
        System.out.println("수정 후 조회 : " + messageService.getMessage(message1.getId()).getContent());

        //삭제
        messageService.deleteMessage(message1.getId());
        System.out.println("삭제 : " + messageService.getMessage(message1.getId()) == null ? "존재함" : "존재하지않음");

    }
}