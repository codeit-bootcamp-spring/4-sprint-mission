package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {

        UserService userService = ServiceFactory.createUserService();
        ChannelService channelService = ServiceFactory.createChannelService();
        MessageService messageService = ServiceFactory.createMessageService();

        //이름 쓰면 user 생성
        User userAlice = userService.addUser(new User("Alice"));
        User userBob = userService.addUser(new User("Bob"));
        User userCharlie = userService.addUser(new User("Charlie"));
        User userDave = userService.addUser(new User("Dave"));
        User userEren = userService.addUser(new User("Eren"));

        //채널 이름과 채널 생성자 쓰면 채널 생성
        Channel channelAcrobatic = channelService.createChannel(new Channel("Acrobatic",userAlice));
        Channel channelBasic = channelService.createChannel(new Channel("Basic", userBob));
        Channel channelCrow = channelService.createChannel(new Channel("Crow", userCharlie));


        //message 메세지 내용과 쓰는 사람 이름, 쓸 채널 이름 쓰면 메세지 생성
        Message message1 = messageService.addMessage(new Message(
                "how",
                userAlice,
                channelAcrobatic
        ));
        Message message11 = messageService.addMessage(new Message(
                "what",
                userAlice,
                channelBasic
        ));

        Message message2 = messageService.addMessage(new Message(
                "to",
                userBob,
                channelBasic
        ));
        Message message22 = messageService.addMessage(new Message(
                "for",
                userBob,
                channelCrow
        ));

        Message message3 = messageService.addMessage(new Message(
                "eat?",
                userCharlie,
                channelCrow
        ));
        Message message33 = messageService.addMessage(new Message(
                "use?",
                userCharlie,
                channelAcrobatic
        ));

        //데이터 조회
        userService.getUsers()
                .forEach(System.out::println);
        System.out.println();

        messageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        channelService.getChannels()
                .forEach(System.out::println);
        System.out.println();

        //유저가 쓴 모든 메세지 출력
        System.out.println(userBob.getUserName() + "'s messages: ");
        userService.findMessagesByUser(userBob)
                .forEach(message -> System.out.println(message.getContent()));
        System.out.println();

        //채널에서 쓴 모든 메세지 출력
        System.out.println(userAlice.getUserName() + "'s messages: ");
        channelService.getMessagesInChannel(channelAcrobatic)
                .forEach(message -> System.out.println(message.getContent()));
        System.out.println();

        //채널에서 유저를 받아 쓴 메세지 출력
        System.out.println(userAlice.getUserName() + "'s messages in "+channelAcrobatic.getChannelName() + ": ");
        channelService.getMessagesByUserInChannel(channelAcrobatic,userAlice)
                .forEach(message -> System.out.println(message.getContent()));
        System.out.println();

        //단건 조회
        Optional<User> result1 = userService.findUserById(userAlice.getUserId());
        if (result1.isPresent()) {
            System.out.println("유저를 찾았습니다: " + result1.get().getUserName());
        }else{
            System.out.println("해당 유저는 존재하지 않습니다.");
        }

        Optional<Channel> result2 = channelService.findChannelById(channelAcrobatic.getChannelId());
        if (result2.isPresent()) {
            System.out.println("채널을 찾았습니다: " + result2.get().getChannelName());
        } else {
            System.out.println("해당 채널이 존재하지 않습니다.");
        }

        Optional<Message> result3 = messageService.findMessageById(message1.getMessageId());
        if(result3.isPresent()){
            System.out.println("메세지를 찾았습니다: " + result3.get().getContent());
        }else{
            System.out.println("해당 메세지가 존재하지 않습니다.");
        }

        System.out.println();

        //포함 조회
        System.out.println("a in Username: ");
        userService.findUsersByNameContains("a")
                .forEach(user ->  System.out.println(user.getUserName()));
        System.out.println();


        //채널에 유저 참가
        channelService.joinUser(channelAcrobatic, userBob);
        channelService.joinUser(channelBasic, userCharlie);
        channelService.joinUser(channelAcrobatic, userDave);
        channelService.joinUser(channelBasic, userDave);
        channelService.joinUser(channelCrow, userDave);

        //채널에서 유저 삭제
        channelService.leaveUser(channelBasic, userDave);
        channelService.leaveUser(channelCrow, userDave);


        userService.getUsers()
                .forEach(System.out::println);
        System.out.println();

        messageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        channelService.getChannels()
                .forEach(System.out::println);
        System.out.println();



        // 수정할 이름 쓰면 수정
        userService.updateUser(userAlice.getUserId(),"Downer");
        channelService.updateChannelName(channelAcrobatic.getChannelId(),"Dean");
        messageService.updateMessage(message3.getMessageId(),"play?");


        // 이름 쓰면 삭제
        userService.deleteUser(userBob);

        messageService.deleteMessage(message2);

        channelService.deleteChannel(channelBasic);


        // 조회를 통해 삭제되었는지 확인
        userService.getUsers()
                .forEach(System.out::println);
        System.out.println();

        messageService.getMessages()
                .forEach(System.out::println);
        System.out.println();

        channelService.getChannels()
                .forEach(System.out::println);
        System.out.println();
    }
}
