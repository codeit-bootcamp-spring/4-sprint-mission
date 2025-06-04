package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.UserStatus.ACTIVE;
import static com.sprint.mission.discodeit.entity.UserStatus.INACTIVE;

public class Application {
    public static void main(String[] args) {
        JCFUserService userService = JCFUserService.getInstance();

        //1.유저 등록
        UUID userId = UUID.randomUUID();
        User user1 = new User(UUID.randomUUID(), "문은서", ACTIVE);
        User user2 = new User(UUID.randomUUID(), "양말", ACTIVE);
        userService.addUser(user1);
        userService.addUser(user2);


        //2. 유저 조회(단건)
        System.out.println("유저를 조회 합니다. ");
        System.out.println(userService.getUsers().get(0));

        //3. 전체 유저 조회(다건)
        System.out.println("전체 유저를 조회합니다. ");
        for (User user: userService.getUsers()) {
            System.out.println(user);
        }

        //4. 유저 이름 수정
        System.out.println("유저명을 수정합니다.");
        userService.updateUser(user1.getUserId(), "코코");

        //5. 수정된 유저 데이터 조회
        System.out.println("수정 후 유저 데이터를 조회합니다. ");
        System.out.println(userService.getUsers());

        //6. 유저 삭제
        System.out.println("유저를 삭제합니다.");
        userService.deleteUser(user1.getUserId());

        //7. 조회를 통한 유저 삭제 확인
        System.out.println("유저 데이터가 삭제되었는지 확인합니다. ");
        System.out.println(userService.getUsers());

        //8. 키워드로 유저 찾기
        System.out.println("키워드로 유저를 찾습니다.");
        userService.findUsersByKeyword("양");

        //9.유저 상태 변경
        System.out.println("유저의 상태를 변경합니다.");
        userService.updateUserStatus(user1.getUserId(), INACTIVE);
        System.out.println(userService.getUsers());

        //10. 상태 별 유저 조회
        System.out.println("[" + ACTIVE + "] 상태인 유저를 찾습니다.");
        for(User user : userService.findUsersByStatus(ACTIVE)) {
            System.out.println(user);
        }


        JCFChannelService channelService = JCFChannelService.getInstance();
     userService.addUser(user1);

    //1. 채널 등록
    UUID channelId = UUID.randomUUID();
    User host = user1;
    Channel channel = new Channel(channelId, "<점메추 모임>", user1);
    channelService.addChannel(channel);

    //2. 채널 조회(단건)
    System.out.println("채널을 조회합니다.");
    System.out.println(channelService.getChannels().get(0));

    //3. 전체 채널 조회(다건)
    System.out.println("전체 채널을 조회합니다.");
    for (Channel ch: channelService.getChannels()) {
        System.out.println(ch);
    }

    //4. 채널명 수정
    System.out.println("채널명을 수정합니다.");
    channelService.updateChannel(channelId, 1, "<저메추 모임>");

    //5. 수정된 채널명 조회
    System.out.println("수정 후 채널 데이터를 조회합니다.");
    System.out.println(channelService.getChannels().get(0));

    //6. 채널 삭제
    System.out.println("채널을 삭제합니다.");
    channelService.deleteChannel(channelId);

    //7. 조회를 통해 채널 삭제 확인
    System.out.println("채널 데이터가 삭제되었는지 확인합니다.");
    System.out.println(channelService.getChannels());

    //8. 채널 참여하기
    channelService.joinChannel(channel.getChannelId(), user2.getUserId());
    System.out.println(channel.getChannelName() +  "\"님이 채널에 참여합니다.\"");

    //9. 채널에서 나가기
    channelService.leaveChannel(channel.getChannelId(), user2.getUserId());
    System.out.println(channel.getChannelName() + "님이 채널에서 나갔습니다.");

    //10. 키워드로 채널 찾기
    System.out.println("키워드로 채널을 찾습니다.");
    channelService.findChannelByKeyword("추");

    JCFMessageService messageService = JCFMessageService.getInstance();
    channelService.joinChannel(channel.getChannelId(), user2.getUserId());


    //1. 메세지 생성
        Message m1 = new Message(UUID.randomUUID(), user1, channel,"점메추 ㅂㅌ");
        Message m2 = new Message(UUID.randomUUID(), user2, channel,"제육 ㄱ");
        Message m3 = new Message(UUID.randomUUID(), user1, channel,"어제 먹음");
        UUID messageId = m1.getMessageId();
        UUID messageId2 = m2.getMessageId();
        UUID messageId3 = m3.getMessageId();
        messageService.addMessage(m1);
        messageService.addMessage(m2);
        messageService.addMessage(m3);

    //2. 메세지 조회(단건)
        System.out.println("메세지를 조회합니다.");
        System.out.println(messageService.getAllMessages().get(0));

    //3. 메세지 조회(다건)
        System.out.println("모든 메세지를 조회합니다.");
        System.out.println(messageService.getAllMessages());

    //4. 메세지 전체 수정
        System.out.println("전체 메세지를 수정합니다");
        messageService.updateMessage(messageId, "저메추");

    //5. 메세지 부분 수정
        System.out.println("메세지를 부분적으로 수정합니다");
        messageService.replaceSubstringInContent(messageId2, "제육", "족발");


    //6. 수정된 메세지 조회
        System.out.println("수정 후 메세지를 조회합니다.");
        System.out.println(messageService.getAllMessages().get(0));

    //7. 메세지 삭제
        System.out.println("메세지를 삭제합니다.");
        messageService.deleteMessage(messageId);

    //8. 조회를 통해 메세지 삭제 확인
        System.out.println("메세지가 삭제되었는지 확인합니다.");
        System.out.println(messageService.getAllMessages());

    //9. 채널명으로 메세지 리스트 조회하기
    System.out.println("채널명으로 메세지를 조회합니다.");
    String channelName = "<저메추 모임>";
    List<Message> messages = messageService.findMessagesByChannelName(channelName);

    if(messages.isEmpty()) {
        System.out.println("채널에서 생성된 메세지가 없습니다.");
    } else {
        System.out.println("[" + channelName + "] 채널에서 조회된 메세지 목록:");
        for(Message m: messages) {
            System.out.println(m);
        }
    }

    //10. 작성자로 메세지 리스트 조회하기
    System.out.println("직성자로 메세지를 조회합니다.");
    User sender = user1;
    List<Message> messageBySender = messageService.findMessagesBySender(sender);

    if(messageBySender.isEmpty()) {
        System.out.println("[" + sender.getUserName() + "] 님이 작성한 메세지가 없습니다.");
    } else {
        System.out.println("[" + sender.getUserName() + "]님이 작성한 메세지:");
        for(Message m: messageBySender) {
            System.out.println(m);
        }
    }
    //11. 키워드로 메세지 조회하기
        System.out.println("키워드로 메세지를 조회합니다.");
        String keyword = "제육";
        List<Message> messageByKeyword = messageService.findMessagesByKeyword(keyword);

        if(messageByKeyword.isEmpty()) {
            System.out.println("[" + keyword + "] 키워드를 포함하는 메세지가 없습니다.");
        } else {
            System.out.println("[" + keyword + "] 키워드를 포함하는 메세지:");
            for(Message m: messageByKeyword) {
                System.out.println(m);
            }
        }
    }
}