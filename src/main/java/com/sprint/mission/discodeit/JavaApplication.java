package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.factory.FileRepositoryFactory;
import com.sprint.mission.discodeit.repository.factory.JCFRepositoryFactory;
import com.sprint.mission.discodeit.repository.factory.RepositoryFactory;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.factory.FileServiceFactory;
import com.sprint.mission.discodeit.service.factory.JCFServiceFactory;
import com.sprint.mission.discodeit.service.factory.ServiceFactory;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        RepositoryFactory fileRepositoryFactory = new FileRepositoryFactory();
        UserRepository userRepository = fileRepositoryFactory.CreateUserRepository();
        MessageRepository messageRepository = fileRepositoryFactory.CreateMessageRepository();
        ChannelRepository channelRepository = fileRepositoryFactory.CreateChannelRepository();

        ServiceFactory factory = new FileServiceFactory();
        UserService userService = factory.createUserService();
        MessageService messageService = factory.createMessageService();
        ChannelService channelService = factory.createChannelService();

        try {
            userService.registUser("홍길동");
            userService.registUser("토마스");
            userService.registUser("김첨지");
            channelService.registChannel("채널 1");
            channelService.registChannel("채널 2");
            channelService.registChannel("채널 3");

            System.out.println("----모든 유저 출력---- ");
            for (User u : userService.findUserByName("*")) {
                //* 입력시 모든 유저 출력
                userService.showUserInfo(u);
            }

            System.out.println();
            System.out.println("----이름이 \"홍길동\"인 유저 출력---- ");
            userService.showUserInfo(userService.findUserByName("홍길동").get(0));

            System.out.println();
            System.out.println("----\"홍길동\"의 계정 삭제 후 모든 유저 출력---- ");
            userService.deleteUser(userService.findUserByName("홍길동").get(0).getUserId());
            for (User u : userService.findUserByName("*")) {
                userService.showUserInfo(u);
            }

            System.out.println();
            System.out.println("----모든 채널 출력----");
            for(Channel c : channelService.findChannelByName("*")) {
                channelService.showChannelInfo(c);
            }

            System.out.println();
            System.out.println("----\"채널 2\" 이름 \"새 채널 2\"로 변경 후 모든 채널 출력----");
            channelService.updateChannelName(channelService.findChannelByName("채널 2").get(0).getChannelId(),"새 채널 2");
            for(Channel c : channelService.findChannelByName("*")) {
                channelService.showChannelInfo(c);
            }


            System.out.println();
            System.out.println("----\"새 채널 2\" 삭제 후 모든 채널 출력----");
            channelService.deleteChannel(channelService.findChannelByName("새 채널 2").get(0).getChannelId());
            for(Channel c : channelService.findChannelByName("*")) {
                channelService.showChannelInfo(c);
            }

            System.out.println();
            System.out.println("----이름이 \"채널 1\"인 모든 채널 출력----");
            for (Channel ch : channelService.findChannelByName("채널 1")) {
                channelService.showChannelInfo(ch);
            }

            Channel testChannel = channelService.findChannelByName("채널 1").get(0);
            System.out.println("----\"채널 1\"에 \"토마스\"와 \"김첨지\" 입장 및 메세지 작성---- ");
            messageService.sendMessage("hello", channelService.findChannelByName("채널 1").get(0), userService.findUserByName("김첨지").get(0));
            messageService.sendMessage("안녕", channelService.findChannelByName("채널 1").get(0), userService.findUserByName("토마스").get(0));
            messageService.sendMessage("배고프다", channelService.findChannelByName("채널 1").get(0), userService.findUserByName("김첨지").get(0));
            //메세지 추가

            System.out.println();
            System.out.println("----\"김첨지\"가 작성한 모든 메세지 출력----");
            for(Message m: userService.getMessageList(userService.findUserByName("김첨지").get(0).getUserId())) {
                messageService.showMessageInfo(m);
            }

            System.out.println();
            System.out.println("----\"채널 1\"에 있는 모든 유저 출력----");
            for (User u : testChannel.getUsers()) {
                userService.showUserInfo(u);
                //채널 1에 있는 모든 유저 출력
            }

            System.out.println();
            System.out.println("----\"채널 1\"에서 \"김첨지\"를 강퇴시킨 후 채널의 모든 유저 및 메세지 출력----");
            channelService.kickUser(channelService.findChannelByName("채널 1").get(0).getChannelId(),userService.findUserByName("김첨지").get(0));
            for (User u : testChannel.getUsers()) {
                userService.showUserInfo(u);
                //채널 1에 있는 모든 유저 출력
            }
            for (Message m : testChannel.getMessages()) {
                messageService.showMessageInfo(m);
                //채널 1에 있는 모든 유저 출력
            }

            System.out.println();
            System.out.println("----\"김첨지\"의 계정 삭제 후 \"채널 1\"의 모든 유저 및 메세지 출력----");
            userService.deleteUser(userService.findUserByName("김첨지").get(0).getUserId());
            for (User u : testChannel.getUsers()) {
                userService.showUserInfo(u);
                //채널 1에 있는 모든 유저 출력
            }
            for (Message m : testChannel.getMessages()) {
                messageService.showMessageInfo(m);
                //채널 1에 있는 모든 유저 출력
            }


            System.out.println();
            System.out.println("----이름이 \"토마스\"인 유저가 채널 3에 입장---- ");
            userService.addNewChannel(userService.findUserByName("토마스").get(0).getUserId(),channelService.findChannelByName("채널 3").get(0));
            System.out.println("----이름이 \"토마스\"인 유저가 참여중인 채널 출력---- ");
            for (Channel c:userService.getChannelsList(userService.findUserByName("토마스").get(0).getUserId())) {
                channelService.showChannelInfo(c);
            }
            System.out.println();
            System.out.println("----\"채널 3\"에 있는 모든 유저 출력---- ");
            for (User u : channelService.findChannelByName("채널 3").get(0).getUsers()) {
                userService.showUserInfo(u);
                //채널 1에 있는 모든 유저 출력
            }

            System.out.println();
            System.out.println("----\"토마스\"의 이름을 \"찰리\"로 변경---- ");
            userService.changeUserName(userService.findUserByName("토마스").get(0).getUserId(),"찰리");
            System.out.println("----\"채널 3\"에 있는 모든 유저 출력---- ");
            for (User u : channelService.findChannelByName("채널 3").get(0).getUsers()) {
                userService.showUserInfo(u);
                //채널 1에 있는 모든 유저 출력
            }

            System.out.println();
            System.out.println("----\"찰리\"의 상태를 Inactive로 변경 후 \"채널 3\"에 있는 모든 유저 출력---- ");
            userService.setUserStatus(userService.findUserByName("찰리").get(0).getUserId(), User.Status.INACTIVE);
            for (User u : channelService.findChannelByName("채널 3").get(0).getUsers()) {
                userService.showUserInfo(u);
                //채널 1에 있는 모든 유저 출력
            }
            System.out.println();
            System.out.println("----Inactive 상태의 \"찰리\"가 글을 작성하려고 시도----");
            messageService.sendMessage("휴면중이지만 글 작성함",channelService.findChannelByName("채널 3").get(0),userService.findUserByName("찰리").get(0));

            Message testMessage = messageService.findMessageByBody("hel").get(0);
            //"hel" 이 들어간 모든 메세지 중 첫번째 메세지

            System.out.println();
            System.out.println("----테스트 메세지 출력----");
            messageService.showMessageInfo(testMessage);
            //메세지 출력
            messageService.fixMessage(testMessage.getMessageId(), "good bye");
            //메세지 내용 수정
            System.out.println();
            System.out.println("----테스트 메세지 수정 후 출력----");
            messageService.showMessageInfo(testMessage);

            System.out.println();
            System.out.println("----테스트 메세지 삭제----");
            messageService.deleteMessage(testMessage.getMessageId());
            //메세지 삭제
            messageService.showMessageInfo(testMessage);
        }

        catch (NoSuchElementException e) {
            System.out.println("에러 발생: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            System.out.println("알 수 없는 에러 발생: " + e.getMessage());
        }

        System.out.println();
        System.out.println("----존재하는 모든 메세지 출력---- ");
        messageService.showAllMessage();

        System.out.println();
        System.out.println("----채널 1 삭제후 존재하는 모든 메세지 출력---- ");
        channelService.deleteChannel(channelService.findChannelByName("채널 1").get(0).getChannelId());
        messageService.showAllMessage();

    }

}
