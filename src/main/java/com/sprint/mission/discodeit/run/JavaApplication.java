package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.domain.ServiceType;
import com.sprint.mission.discodeit.domain.UserType;
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
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;


public class JavaApplication {


    public static void main(String[] args) {

        // File Service
        ServiceFactory fileFactory = new ServiceFactory(ServiceType.ServiceStatus.FILE);
        ChannelService fileChannelService = fileFactory.getChannelService();
        UserService fileUserService = fileFactory.getUserService();
        MessageService fileMessageService = fileFactory.getMessageService();

        // User FileI/O
        // Create
        System.out.println("<File Service>\n");
        System.out.println("\n<File User Create>");
        User user1 = fileUserService.createUser("user1");
        User user2 = fileUserService.createUser("user2");

        // Read
        System.out.println("\n<File User Read>");
        System.out.println("getUsers(): " + fileUserService.getUsers());
        System.out.println("getUserById(): " + fileUserService.getUserById(user1.getId()));

        // Update
        System.out.println("\n<File User Update>");
        fileUserService.updateActiveUserNameByUser(user1, "firstUser");
        fileUserService.updateUserStatusByUserExceptQuitUser(user1, UserType.UserStatus.SLEEP);
        System.out.println("getUserById(): " + fileUserService.getUserById(user1.getId()));
        System.out.println(user1);

        // Delete
        System.out.println("\n<File User Delete>");
        fileUserService.deleteUserByUser(user1);
        System.out.println("getUsers(): " + fileUserService.getUsers());
        System.out.println(user1);


        // Channel FileI/O
        // Create
        System.out.println("<File Chanel Create> \n");
        // error: Channel channel1 = fileChannelService.createChannel("channel1", user1);
        Channel channel2 = fileChannelService.createChannel("channel2", user2);
        Channel channel3 = fileChannelService.createChannel("channel3", user2);
        System.out.println("\"" + channel2.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel2.getId()).getUsers());
        System.out.println("\"" + user2.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user2.getId()).getChannels());

        // Read
        System.out.println("\n<File Channel Read>");
        System.out.println("콘솔 출력: " + channel2);
        System.out.println("getChannels(): " + fileChannelService.getChannels());
        System.out.println("getChannelById(): " + fileChannelService.getChannelById(channel2.getId()));

        // Update
        System.out.println("\n<File Channel Update>");
        fileChannelService.updateChannelNameByChannel(channel2, "myFirstChannel");
        System.out.println(fileChannelService.getChannelById(channel2.getId()));
        System.out.println(channel2);
        System.out.println("\"" + channel2.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel2.getId()).getUsers());
        System.out.println("\"" + user2.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user2.getId()).getChannels());

        // Delete
        System.out.println("\n<File Channel Delete>");
        fileChannelService.deleteChannelByChannelAndHostUser(channel2, user2);
        System.out.println("getChannels(): " + fileChannelService.getChannels());
        System.out.println(channel2);
        System.out.println("\"" + user2.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user2.getId()).getChannels());

        // error test
        Channel channel4 = new Channel("channel4", user2.getId());
        fileChannelService.deleteChannelByChannelAndHostUser(channel4, user2);


        // Message FileI/O
        // Create
        System.out.println("\n<File Message Create>");
        // error: Message message1 = messageService.createMessage("message1", user2, channel2);
        Message message2 = fileMessageService.createMessage("Message2", user2, channel3);
        Message message3 = fileMessageService.createMessage("Message3", user2, channel3);
        System.out.println(user2.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user2.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());

        // Read
        System.out.println("\n<File Message Read>");
        System.out.println("getMessages(): " + fileMessageService.getMessages());
        System.out.println("getMessageById(): " + fileMessageService.getMessageById(message2.getId()));

        // Update
        System.out.println("\n<File Message Upadte>");
        fileMessageService.updateMessageContentsByMessage(message2, user2, "old message");
        System.out.println("getMessageById(): " + fileMessageService.getMessageById(message2.getId()));
        System.out.println(message2);
        System.out.println(user2.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user2.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());

        // Delete
        System.out.println("\n<File Message Delete>");
        fileMessageService.deleteMessageByMessage(message2, user2, channel3);
        System.out.println("getMessages(): " + fileMessageService.getMessages());
        System.out.println(user2.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user2.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());


        // User 동시성 실험
        System.out.println("\n<File User Concurrency>");
        User user4 = fileUserService.createUser("user4");
        System.out.println("user4: \"" + user4.getUserName() + "\" id: [" + user4.getId() + "]");

        System.out.println("\n<File joinChannel(user4, channel3)>");
        fileUserService.joinChannelOnlyActiveUser(user4, channel3);
        Message message4 = fileMessageService.createMessage("Message4", user4, channel3);
        fileUserService.updateActiveUserNameByUser(user4, "forthUser");
        // (error: outChannel 안됨) userService.updateUserStatusByUser(user4, User.Status.SLEEP);

        System.out.println("\"" + user4.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user4.getId()).getChannels());
        System.out.println("\"" + channel3.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel3.getId()).getUsers());
        System.out.println(user4.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user4.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());

        System.out.println("\n<File outChannel(user4, channel3)>");
        fileUserService.outChannelOnlyActiveUser(user4, channel3);
        System.out.println("\"" + user4.getUserName() + "\"님이 가입한 채널 목록: " + fileUserService.getUserById(user4.getId()).getChannels());
        System.out.println("\"" + channel3.getChannelName() + "\"가 보유한 유저 목록: " + fileChannelService.getChannelById(channel3.getId()).getUsers());
        System.out.println(user4.getUserName() + "가 보낸 메시지 목록" + fileUserService.getUserById(user4.getId()).getMessages());
        System.out.println(channel3.getChannelName() + "에 보내진 메시지 목록" + fileChannelService.getChannelById(channel3.getId()).getMessages());


        System.out.println("\n<File All Delete - User>");
        fileUserService.deleteAllUsers();
        System.out.println("getUsers(): " + fileUserService.getUsers());

        System.out.println("\n<File All Delete - Channel>");
        fileChannelService.deleteAllChannels();
        System.out.println("getChannels(): " + fileChannelService.getChannels());

        System.out.println("\n<File All Delete - Message>");
        fileMessageService.deleteAllMessages();
        System.out.println("getMessages(): " + fileMessageService.getMessages());


        // JCFService
        ServiceFactory jcfServiceFactory = new ServiceFactory(ServiceType.ServiceStatus.JCF);
        UserService jcfUserService = jcfServiceFactory.getUserService();
        ChannelService jcfChannelService = jcfServiceFactory.getChannelService();
        MessageService jcfMessageService = jcfServiceFactory.getMessageService();

        System.out.println("\n\n<JCF Service>\n");
        System.out.println("\n<JCF Create>");
        User jcfUser1 = jcfUserService.createUser("jcfUser1");
        User jcfUser2 = jcfUserService.createUser("jcfUser2");
        Channel jcfChannel1 = jcfChannelService.createChannel("jcfChanel1", jcfUser1);
        Message jcfMessage1 = jcfMessageService.createMessage("Message1", jcfUser1, jcfChannel1);
        // error: Message jcfMessage2 = jcfMessageService.createMessage("Message2", jcfUser2, jcfChannel1);

        System.out.println("\n<JCF Read>");
        System.out.println(jcfUser1.getUserName() + "가 가입한 채널: " + jcfUser1.getChannels());
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println(jcfUser1.getUserName() + "가 보낸 메시지 " + jcfUser1.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF Update>");
        jcfUserService.updateActiveUserNameByUser(jcfUser1, "JcfServiceUser1");
        jcfUserService.updateUserStatusByUserExceptQuitUser(jcfUser1, UserType.UserStatus.SLEEP);
        System.out.println(jcfUserService.getUserById(jcfUser1.getId()));
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println("\n<JCF Delete>");
        jcfUserService.updateUserStatusByUserExceptQuitUser(jcfUser1, UserType.UserStatus.ACTIVE);
        jcfUserService.deleteUserByUser(jcfUser1);

        System.out.println(jcfUser1.getUserName() + "가 가입한 채널: " + jcfUser1.getChannels());
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println(jcfUser1.getUserName() + "가 보낸 메시지 " + jcfUser1.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF Join Channel>");
        jcfUserService.joinChannelOnlyActiveUser(jcfUser2, jcfChannel1);
        Message jcfMessage3 = jcfMessageService.createMessage("jcfMessage3", jcfUser2, jcfChannel1);

        System.out.println(jcfUser2.getUserName() + "가 보낸 메시지 " + jcfUser2.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF Out Channel>");
        jcfUserService.outChannelOnlyActiveUser(jcfUser2, jcfChannel1);
        System.out.println(jcfUser2.getUserName() + "가 가입한 채널: " + jcfUser2.getChannels());
        System.out.println(jcfChannel1.getChannelName() + "의 유저: " + jcfChannel1.getUsers());

        System.out.println(jcfUser2.getUserName() + "가 보낸 메시지 " + jcfUser2.getMessages());
        System.out.println(jcfChannel1.getChannelName() + "에서 보낸 메시지: " + jcfChannel1.getMessages());

        System.out.println("\n<JCF All Delete - User>");
        jcfUserService.deleteAllUsers();
        System.out.println(jcfUserService.getUsers());

        System.out.println("\n<JCF All Delete - Channel>");
        jcfChannelService.deleteAllChannels();
        System.out.println(jcfChannelService.getChannels());

        System.out.println("\n<JCF All Delete - Message>");
        jcfMessageService.deleteAllMessages();
        System.out.println(jcfMessageService.getMessages());

        // Basic Service
        System.out.println("\n<Basic Service Create>");
        // File Repository
        UserRepository fileUserRepository = new FileUserRepository();
        ChannelRepository fileChannelRepository = new FileChannelRepository();
        MessageRepository fileMessageRepository = new FileMessageRepository();

        // JCF Repository
        UserRepository jcfUserRepository = new JCFUserRepository();
        ChannelRepository jcfChannelRepository = new JCFChannelRepository();
        MessageRepository jcfMessageRepository = new JCFMessageRepository();

        // File Basic Service
        BasicUserService basicFileUserService = new BasicUserService(fileUserRepository);
        BasicChannelService basicFileChannelService = new BasicChannelService(fileChannelRepository);
        BasicMessageService basicFileMessageService = new BasicMessageService(fileMessageRepository);

        // JCF Basic Service
        BasicUserService basicJCFUserService = new BasicUserService(jcfUserRepository);
        BasicChannelService basicJCFChannelService = new BasicChannelService(jcfChannelRepository);
        BasicMessageService basicJCFMessageService = new BasicMessageService(jcfMessageRepository);


        System.out.println("\n<Basic Create>");
        System.out.println("\n<File Create>");
        // User
        User basicFileUser1 = basicFileUserService.createUser("basicFileUser1");
        User basicFileUser2 = basicFileUserService.createUser("basicFileUser2");
        User basicFileUser3 = basicFileUserService.createUser("basicFileUser3");

        // Channel
        Channel basicFileChannel1 = basicFileChannelService.createChannel("basicFileChannel1", basicFileUser1);
        Channel basicFileChannel2 = basicFileChannelService.createChannel("basicFileChannel2", basicFileUser1);
        Channel basicFileChannel3 = basicFileChannelService.createChannel("basicFileChannel3", basicFileUser3);

        // Message
        Message basicFileMessage1 = basicFileMessageService.createMessage("basicFileMessage1", basicFileUser1, basicFileChannel1);


        System.out.println("\n<JCF Create>");
        // User
        User basicJCFUser1 = basicJCFUserService.createUser("basicJCFUser1");
        User basicJCFUser2 = basicJCFUserService.createUser("basicJCFUser2");
        User basicJCFUser3 = basicJCFUserService.createUser("basicJCFUser3");

        // Channel
        Channel basicJCFChannel1 = basicJCFChannelService.createChannel("basicJCFChannel1", basicJCFUser1);
        Channel basicJCFChannel2 = basicJCFChannelService.createChannel("basicJCFChannel2", basicJCFUser1);
        Channel basicJCFChannel3 = basicJCFChannelService.createChannel("basicJCFChannel3", basicJCFUser3);

        // Message
        Message basicJCFMessage1 = basicJCFMessageService.createMessage("basicJCFMessage1", basicJCFUser1, basicJCFChannel1);


        System.out.println("\n<Basic Read>");
        System.out.println("\n<File Read>");
        // getAll
        System.out.println(basicFileUserService.getUsers());
        System.out.println(basicFileChannelService.getChannels());
        System.out.println(basicFileMessageService.getMessages());

        // getById
        System.out.println(basicFileUserService.getUserById(basicFileUser1.getId()));
        System.out.println(basicFileChannelService.getChannelById(basicFileChannel1.getId()));
        System.out.println(basicFileMessageService.getMessageById(basicFileMessage1.getId()));

        System.out.println("\n<JCF Read>");
        // getAll
        System.out.println(basicJCFUserService.getUsers());
        System.out.println(basicJCFChannelService.getChannels());
        System.out.println(basicJCFMessageService.getMessages());

        // getById
        System.out.println(basicJCFUserService.getUserById(basicJCFUser1.getId()));
        System.out.println(basicJCFChannelService.getChannelById(basicJCFChannel1.getId()));
        System.out.println(basicJCFMessageService.getMessageById(basicJCFMessage1.getId()));

        System.out.println("\n<Basic Update>");
        System.out.println("\n<File Update>");
        // User
        basicFileUserService.updateActiveUserNameByUser(basicFileUser1, "myFirstBasicFileUser");
        System.out.println(basicFileUserService.getUserById(basicFileUser1.getId()));

        // Channel
        basicFileChannelService.updateChannelNameByChannel(basicFileChannel1, "myFirstBasicFileChannel");
        System.out.println(basicFileChannelService.getChannelById(basicFileChannel1.getId()));

        // Message
        basicFileMessageService.updateMessageContentsByMessage(basicFileMessage1, basicFileUser1, "myFirstBasicFileMessage");
        System.out.println(basicFileMessageService.getMessageById(basicFileMessage1.getId()));


        System.out.println("\n<JCF Update>");
        // User
        basicJCFUserService.updateActiveUserNameByUser(basicJCFUser1, "myFirstJCFUser");
        System.out.println(basicJCFUserService.getUserById(basicJCFUser1.getId()));

        // Channel
        basicJCFChannelService.updateChannelNameByChannel(basicJCFChannel1, "myFirstJCFChannel");
        System.out.println(basicJCFChannelService.getChannelById(basicJCFChannel1.getId()));

        // Message
        basicJCFMessageService.updateMessageContentsByMessage(basicJCFMessage1, basicJCFUser1, "myFirstJCFMessage");
        System.out.println(basicJCFMessageService.getMessageById(basicJCFMessage1.getId()));

        System.out.println("\n<Basic Delete>");
        System.out.println("\n<File Delete>");
        // User
        basicFileUserService.deleteUserByUser(basicFileUser2);
        System.out.println(basicFileUserService.getUsers());

        // Channel
        basicFileChannelService.deleteChannelByChannelAndHostUser(basicFileChannel2, basicFileUser1);
        System.out.println(basicFileChannelService.getChannels());

        // Message
        basicFileMessageService.deleteMessageByMessage(basicFileMessage1, basicFileUser1, basicFileChannel1);
        System.out.println(basicFileMessageService.getMessages());

        System.out.println("\n<JCF Delete>");
        // User
        basicJCFUserService.deleteUserByUser(basicJCFUser2);
        System.out.println(basicJCFUserService.getUsers());

        // Channel
        basicJCFChannelService.deleteChannelByChannelAndHostUser(basicJCFChannel2, basicJCFUser1);
        System.out.println(basicJCFChannelService.getChannels());

        // Message
        basicJCFMessageService.deleteMessageByMessage(basicJCFMessage1, basicJCFUser1, basicJCFChannel1);
        System.out.println(basicJCFMessageService.getMessages());

        System.out.println("\n<Basic Concurrency>");
        System.out.println("\n<File Concurrency>");
        // User
        basicFileUserService.joinChannelOnlyActiveUser(basicFileUser1, basicFileChannel3);
        basicFileUserService.updateActiveUserNameByUser(basicFileUser3, "myThirdBasicFileUser");
        System.out.println("\"" + basicFileChannel3.getChannelName() + "\"에 접속한 유저 목록: " + basicFileChannelService.getChannelById(basicFileChannel3.getId()).getUsers());

        // Channel
        basicFileChannelService.updateChannelNameByChannel(basicFileChannel3, "myThirdBasicFileChannel");
        System.out.println("\n\"" + basicFileUser1.getUserName() + "\"님이 가입한 채널 목록: " + basicFileUserService.getUserById(basicFileUser1.getId()).getChannels());
        System.out.println("\"" + basicFileUser3.getUserName() + "\"님이 가입한 채널 목록: " + basicFileUserService.getUserById(basicFileUser3.getId()).getChannels());
        
        
        // Message
        Message basicFileMessage2 = basicFileMessageService.createMessage("basicFileMessage2", basicFileUser1, basicFileChannel3);
        Message basicFileMessage3 = basicFileMessageService.createMessage("basicFileMessage3", basicFileUser3, basicFileChannel3);
        basicFileMessageService.updateMessageContentsByMessage(basicFileMessage2, basicFileUser1, "mySecondBasicFileMessage");
        System.out.println("\n\"" + basicFileChannel3.getChannelName() + "\"에 작성된 메시지 목록: " + basicFileChannelService.getChannelById(basicFileChannel3.getId()).getMessages());
        System.out.println("\"" + basicFileUser1.getUserName() + "\"님이 작성하신 메시지 목록: " + basicFileUserService.getUserById(basicFileUser1.getId()).getMessages());

        // outChannel
        basicFileUserService.outChannelOnlyActiveUser(basicFileUser1, basicFileChannel3);
        System.out.println("\"" + basicFileChannel3.getChannelName() + "\"에 접속한 유저 목록: " + basicFileChannelService.getChannelById(basicFileChannel3.getId()).getUsers());
        System.out.println("\n\"" + basicFileChannel3.getChannelName() + "\"에 작성된 메시지 목록: " + basicFileChannelService.getChannelById(basicFileChannel3.getId()).getMessages());
        System.out.println("\"" + basicFileUser1.getUserName() + "\"님이 작성하신 메시지 목록: " + basicFileUserService.getUserById(basicFileUser1.getId()).getMessages());
        
        System.out.println("\n<JCF Concurrency>");
        // User
        basicJCFUserService.joinChannelOnlyActiveUser(basicJCFUser1, basicJCFChannel3);
        basicJCFUserService.updateActiveUserNameByUser(basicJCFUser3, "myThirdBasicJCFUser");
        System.out.println("\"" + basicJCFChannel3.getChannelName() + "\"에 접속한 유저 목록: " + basicJCFChannelService.getChannelById(basicJCFChannel3.getId()).getUsers());

        // Channel
        basicJCFChannelService.updateChannelNameByChannel(basicJCFChannel3, "myThirdBasicJCFChannel");
        System.out.println("\n\"" + basicJCFUser1.getUserName() + "\"님이 가입한 채널 목록: " + basicJCFUserService.getUserById(basicJCFUser1.getId()).getChannels());
        System.out.println("\"" + basicJCFUser3.getUserName() + "\"님이 가입한 채널 목록: " + basicJCFUserService.getUserById(basicJCFUser3.getId()).getChannels());

        // Message
        Message basicJCFMessage2 = basicJCFMessageService.createMessage("basicJCFMessage2", basicJCFUser1, basicJCFChannel3);
        Message basicJCFMessage3 = basicJCFMessageService.createMessage("basicJCFMessage3", basicJCFUser3, basicJCFChannel3);
        basicJCFMessageService.updateMessageContentsByMessage(basicJCFMessage2, basicJCFUser1, "mySecondBasicJCFMessage");
        System.out.println("\n\"" + basicJCFChannel3.getChannelName() + "\"에 작성된 메시지 목록: " + basicJCFChannelService.getChannelById(basicJCFChannel3.getId()).getMessages());
        System.out.println("\"" + basicJCFUser1.getUserName() + "\"님이 작성하신 메시지 목록: " + basicJCFUserService.getUserById(basicJCFUser1.getId()).getMessages());

        // outChannel
        basicJCFUserService.outChannelOnlyActiveUser(basicJCFUser1, basicJCFChannel3);
        System.out.println("\n\"" + basicJCFChannel3.getChannelName() + "\"에 접속한 유저 목록: " + basicJCFChannelService.getChannelById(basicJCFChannel3.getId()).getUsers());
        System.out.println("\"" + basicJCFChannel3.getChannelName() + "\"에 작성된 메시지 목록: " + basicJCFChannelService.getChannelById(basicJCFChannel3.getId()).getMessages());
        System.out.println("\"" + basicJCFUser1.getUserName() + "\"님이 작성하신 메시지 목록: " + basicJCFUserService.getUserById(basicJCFUser1.getId()).getMessages());
    }
}
