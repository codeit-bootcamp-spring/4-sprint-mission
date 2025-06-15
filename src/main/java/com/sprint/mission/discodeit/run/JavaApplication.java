package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JavaApplication {
    public static void init(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {}
        }
    }
    public static void main(String[] args) {
        //[JCFService]
//        UserService userService = JCFUserService.getInstance();
//        ChannelService channelService = JCFChannelService.getInstance();
//        MessageService messageService = JCFMessageService.getInstance();

        //[FileService]
//        UserService userService = FileUserService.getInstance();
//        ChannelService channelService = FileChannelService.getInstance();
//        MessageService messageService = FileMessageService.getInstance();


        //[Repository]
        UserRepository userRepository = FileUserRepository.getInstance();
        ChannelRepository channelRepository = FileChannelRepository.getInstance();
        MessageRepository messageRepository = FileMessageRepository.getInstance();

//        UserRepository userRepository = JCFUserRepository.getInstance();
//        ChannelRepository channelRepository = JCFChannelRepository.getInstance();
//        MessageRepository messageRepository = JCFMessageRepository.getInstance();

        //[BasicService]
        BasicUserService userService = BasicUserService.getInstance(userRepository);
        BasicChannelService channelService = BasicChannelService.getInstance(channelRepository);
        BasicMessageService messageService = BasicMessageService.getInstance(messageRepository);

        //[dir init]
        Path baseDir = Paths.get(System.getProperty("user.dir"), "data");
        init(baseDir);

        Path userDir = baseDir.resolve("users");
        Path channelDir = baseDir.resolve("channels");
        Path messageDir = baseDir.resolve("messages");

//      ======================================테스트============================================

        //[생성 / 저장]
            //[유저]
            User user1 = userService.saveUser(userDir, new User("firstUser"));
            User user2 = userService.saveUser(userDir, new User("secondUser"));
            //[채널]
            Channel channel1 = channelService.saveChannel(channelDir, new Channel("firstChannel", user1));
            Channel channel2 = channelService.saveChannel(channelDir, new Channel("secondChannel", user2));
            //[메세지]
            Message message1 = messageService.sendMessage(messageDir, new Message("firstMessage", user1, channel1));
            Message message2 = messageService.sendMessage(messageDir, new Message("secondMessage", user2, channel2));
        //[조회]
            //[유저]
                //[다건]
                userService.loadAllUsers(userDir).forEach(user -> System.out.println(user.toString()));
                //[단건]
                    //[ID]
                    userService.loadUserById(userDir, user1.getId())
                            .ifPresent(user -> System.out.println(user.toString()));
                    userService.loadUserById(userDir, user2.getId())
                            .ifPresent(user -> System.out.println(user.toString()));
                    //[NAME]
                    userService.loadUserByUsername(userDir, user1.getUserName())
                            .ifPresent(user -> System.out.println(user.toString()));
                    userService.loadUserByUsername(userDir, user2.getUserName())
                            .ifPresent(user -> System.out.println(user.toString()));
            //[채널]
                //[다건]
                channelService.loadAllChannels(channelDir)
                        .forEach(channel -> System.out.println(channel.toString()));
                //[단건]
                    //[ID}
                    channelService.loadChannelById(channelDir, channel1.getId())
                            .ifPresent(channel -> System.out.println(channel.toString()));
                    channelService.loadChannelById(channelDir, channel2.getId())
                            .ifPresent(channel -> System.out.println(channel.toString()));
                    //[NAME]
                    channelService.loadChannelByChannelName(channelDir, channel1.getChannelName())
                            .ifPresent(channel -> System.out.println(channel.toString())) ;
                    channelService.loadChannelByChannelName(channelDir, channel2.getChannelName())
                            .ifPresent(channel -> System.out.println(channel.toString())) ;
            //[메세지]
                //[다건]
                messageService.loadAllMessages(messageDir)
                        .forEach(message -> System.out.println(message.toString()));
                //[단건]
                messageService.loadMessageById(messageDir, message1.getId())
                        .ifPresent(message -> System.out.println(message.toString()));
                messageService.loadMessageById(messageDir, message2.getId())
                        .ifPresent(message -> System.out.println(message.toString()));
        //join, leave Channel
            //[join]
            userService.joinChannel(userDir, channelDir, user1, channel2);
            userService.joinChannel(userDir, channelDir, user2, channel1);
            //[확인]
                //[유저]
                userService.loadUserById(userDir, user1.getId())
                        .ifPresent(user -> System.out.println(user.toString()));
                userService.loadUserById(userDir, user2.getId())
                        .ifPresent(user -> System.out.println(user.toString()));
                //[채널]
                channelService.loadChannelById(channelDir, channel1.getId())
                        .ifPresent(channel -> System.out.println(channel.toString()));
                channelService.loadChannelById(channelDir, channel2.getId())
                        .ifPresent(channel -> System.out.println(channel.toString()));
            //[leave]
            userService.leaveChannel(userDir, channelDir, user1, channel2);
            userService.leaveChannel(userDir, channelDir, user2, channel1);
            //[확인]
                //[유저]
                userService.loadUserById(userDir, user1.getId())
                        .ifPresent(user -> System.out.println(user.toString()));
                userService.loadUserById(userDir, user2.getId())
                        .ifPresent(user -> System.out.println(user.toString()));
                //[채널]
                channelService.loadChannelById(channelDir, channel1.getId())
                        .ifPresent(channel -> System.out.println(channel.toString()));
                channelService.loadChannelById(channelDir, channel2.getId())
                        .ifPresent(channel -> System.out.println(channel.toString()));
        //[수정]
            //[유저]
            userService.updateUserById(userDir, user1.getId(), "1");
            //[채널]
            channelService.updateChannelById(channelDir, channel1.getId(), "1");
        //[수정 조회]
            //[유저]
            userService.loadUserById(userDir, user1.getId())
                    .ifPresent(user -> System.out.println(user.toString()));
            //[채널]
            channelService.loadChannelById(channelDir, channel1.getId())
                    .ifPresent(channel -> System.out.println(channel.toString()));
        //[삭제]
            //[유저]
            userService.deleteUser(userDir, user1);
            userService.deleteUser(userDir, user2);
            //[채널]
            channelService.deleteChannel(channelDir, channel1);
            channelService.deleteChannel(channelDir, channel2);
            //[메세지]
            messageService.deleteMessage(messageDir, message1);
            messageService.deleteMessage(messageDir, message2);
    }
}