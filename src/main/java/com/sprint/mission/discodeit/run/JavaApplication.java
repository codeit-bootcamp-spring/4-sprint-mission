package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Status;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.factory.Factory;

import java.util.Optional;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        Factory factory = Factory.getInstance();
        UserService jcfUserService = factory.getUserService();
        ChannelService jcfChannelService = factory.getChannelService();
        MessageService jcfMessageService = factory.getMessageService();

        /****************************************
         *  정상 데이터 테스트
         ****************************************/

        System.out.println("------------- 1. USER 테스트 ------------");
        System.out.println("---------등록---------");
        User u1 = jcfUserService.createUser("김코딩","password", "email1@abc.df");
        User u2 = jcfUserService.createUser("이코드", "password2", "abcd@email.com");
        User u3 = jcfUserService.createUser("박자바","pswd123", "java@email.com");
        System.out.println();

        System.out.println("---------다건조회---------");
        jcfUserService.getUsers().
                forEach(System.out::println);
        System.out.println();

        System.out.println("---------단건조회---------");
        Optional<User> targetUser = jcfUserService.getUsersById(u1.getId());
        if (targetUser.isPresent()) {
            System.out.println(targetUser);
        } else {
            System.out.println("해당 유저 없음");
        }
        System.out.println();

        System.out.println("---------전체 조회 (수정 전)---------");
        jcfUserService.getUsers().
                forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (수정 후)---------");
        jcfUserService.updateUser(u1.getId(), new User("최객체",null,null,Status.AWAY));
        jcfUserService.updateUser(u2.getId(), new User(null,"passwordChange","ChangeMail@email.com",null));
        jcfUserService.getUsers().
                forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (삭제 전)---------");
        jcfUserService.getUsers().
                forEach(System.out::println);
        jcfUserService.deleteUser(u1.getId());
        System.out.println();

        System.out.println("---------전체 조회 (삭제 후)---------");
        jcfUserService.getUsers().
                forEach(System.out::println);
        System.out.println();

        /*********************************
         * 채널 테스트
         *********************************/

        System.out.println("------------------2. Channel 테스트------------------");
        System.out.println("---------등록---------");
        Channel ch1 = jcfChannelService.createChannel(u1,"채널1");
        Channel ch2 = jcfChannelService.createChannel(u2,"채널2");
        Channel ch3 = jcfChannelService.createChannel(u1,"채널3");
        System.out.println();

        System.out.println("---------다건 조회---------");
        jcfChannelService.getChannels()
                .forEach(System.out::println);
        System.out.println();

        System.out.println("---------단건 조회---------");
        Optional<Channel> targetChannel = jcfChannelService.getChannelById(ch2.getId());
        targetChannel.ifPresentOrElse(System.out::println, () -> System.out.println("해당 채널 없음"));
        System.out.println();

        System.out.println("---------전체 조회 (수정 전)---------");
        jcfChannelService.getChannels().
                forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (수정 후)---------");
        jcfChannelService.updateChannel(ch2.getId(),"수정된 채널");
        jcfChannelService.getChannels()
                .forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (삭제 전)---------");
        jcfChannelService.getChannels()
                .forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (삭제 후)---------");
        jcfChannelService.deleteChannel(ch1);
        jcfChannelService.getChannels()
                .forEach(System.out::println);
        System.out.println();

        System.out.println("--------- 채널 내 유저 추가 ---------");
        jcfChannelService.getChannels()
                .forEach(channel -> System.out.println(channel.getUserNames()));
        jcfChannelService.addUserToChannel(ch2,u3);
        jcfChannelService.addUserToChannel(ch3,u3);
        System.out.println();

        System.out.println("--------- 유저 추가 후 ---------");
        jcfChannelService.getChannels()
                .forEach(channel -> System.out.println(channel.getUserNames()));
        System.out.println();

        System.out.println("--------- 채널 내 유저 삭제 ---------");
        jcfChannelService.removeUserFromChannel(ch2,u3.getId());
        jcfChannelService.getChannels()
                .forEach(channel -> System.out.println(channel.getUserNames()));
        System.out.println();

        /**********************************************
         * 메세지 테스트
         **********************************************/

        System.out.println("------------------3. Message 테스트------------------");
        System.out.println("---------등록---------");

        Message msg1 = jcfMessageService.createMessage("내용1",u1,ch1);
        Message msg2 = jcfMessageService.createMessage("내용2",u2,ch1);
        Message msg3 = jcfMessageService.createMessage("내용3",u1,ch2);
        Message msg4 = jcfMessageService.createMessage("내용4",u3,ch2);
        Message msg5 = jcfMessageService.createMessage("내용5",u2,ch2);
        System.out.println();

        System.out.println("---------다건 조회---------");
        jcfMessageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        System.out.println("---------단건 조회---------");
        jcfMessageService.getMessagesById(msg4.getId())
                .ifPresentOrElse(System.out::println,()-> System.out.println("해당 메시지 없음"));
        System.out.println();

        System.out.println("---------전체 조회 (수정 전)---------");
        jcfMessageService.getMessages().
                forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (수정 후)---------");
        jcfMessageService.updateMessage(msg4.getId(),"수정된 내용입니다.");
        jcfMessageService.getMessages().
                forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (삭제 전)---------");
        jcfMessageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        System.out.println("---------전체 조회 (삭제 후)---------");
        jcfMessageService.removeMessage(msg2);
        jcfMessageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        /***********************************************
         * 전체 조회
         ***********************************************/

        System.out.println("------------------4. 전체 조회------------------");
        System.out.println("---------유저 조회---------");
        jcfUserService.getUsers()
                .forEach(System.out::println);

        System.out.println("---------채널 조회---------");
        jcfChannelService.getChannels()
                .forEach(System.out::println);

        System.out.println("---------메세지 조회---------");
        jcfMessageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        /***********************************
         * 유저 삭제 시 채널 및 메시지 삭제 확인
         ***********************************/
        System.out.println("------------------ 5. 유저 삭제 시 채널 내 유저 삭제 확인 ------------------");

        User testUser1 = jcfUserService.createUser("유저1","testPW1","TEST@email.com");
        User testUser2 = jcfUserService.createUser("유저2","TESTpw2","testmail@email.com");
        User testUser3 = jcfUserService.createUser("유저3","testPSWD","email@pass.word");

        Channel testChannel = jcfChannelService.createChannel(testUser1,"테스트채널");
        jcfChannelService.addUserToChannel(testChannel,testUser2);
        jcfChannelService.addUserToChannel(testChannel,testUser3);
        System.out.println(testChannel.getUserNames());
        System.out.println("유저3 삭제");
        jcfUserService.deleteUser(testUser3.getId());

        System.out.println(testChannel.getUserNames());

        /****************************************
         *  존재하지 않는 필드 테스트용 더미 값
         ****************************************/

        System.out.println("------------------ 더미 데이터 ------------------");

        UUID tempUID = UUID.randomUUID();
        User tempUser = new User("더미","tempPW","TEMPTEMP@TEMP.TEMP");
        Channel tempChannel = new Channel(tempUser,"더미채널");
        Message tempMessage = new Message("더미내용",tempUser,tempChannel);

        // 실존 채널에 더미 유저 추가 ( users에 안들어가 있음 )
        jcfChannelService.addUserToChannel(testChannel,tempUser);

        // 각종 더미 메시지
        jcfMessageService.createMessage("더미유저의 메시지",tempUser,tempChannel);
        jcfMessageService.createMessage("실존 유저이 더미 채널에 메시지",testUser1,tempChannel);

        System.out.println(tempChannel.getUserNames());
        System.out.println("<UNK> <UNK> <UNK>");
        System.out.println(testUser1.getMessageContents());
        System.out.println(testChannel.getUserNames());
        System.out.println("----------------------메시지 삭제 확인--------------------------");
        System.out.println(testUser2.getChannelNames());
        testChannel.removeUser(testUser2);
        System.out.println(testUser2.getChannelNames());
        System.out.println("---");
        Message m1 = jcfMessageService.createMessage("<UNK> <UNK>",testUser1,testChannel);
        Message m2 = jcfMessageService.createMessage("<TEST> TEST CONTENT",testUser2,testChannel);
        System.out.println(testChannel.getMessageContents());
        testChannel.removeMessage(m1);
        System.out.println(testChannel.getMessageContents());
        System.out.println("----------------------유저 삭제 후 채널, 채널에서 유저 삭제 후 유저가 참여중인 채널 확인-----------------------");
        System.out.println(testChannel.getUserNames());
        testChannel.addUser(testUser2);
        testChannel.addUser(testUser3);
        jcfUserService.leaveChannel(testUser3,testChannel);
        System.out.println(testChannel.getUserNames());
        testChannel.removeUser(testUser2);
        System.out.println(testChannel.getUserNames());
        System.out.println(testUser2.getChannelNames());
        System.out.println();
    }
}