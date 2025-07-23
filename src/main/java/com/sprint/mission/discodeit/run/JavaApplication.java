//package com.sprint.mission.discodeit.run;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.PresenceStatus;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public class JavaApplication {
//    static User setupUser(UserService userService) {
//        User user = userService.createUser("woody", "woody1234", "woody@codeit.com");
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService, User user) {
//        Channel channel = channelService.createChannel(user, "공지");
//        return channel;
//    }
//
//    static User testUserCreate(UserService userService) {
//        User user = userService.createUser("TEST","PASSWORD12","TEST@EMAIL.COM");
//        return user;
//    }
//
//    static Channel testChannelCreate(ChannelService channelService, User user) {
//        Channel channel = channelService.createChannel(user, "TEST");
//        return channel;
//    }
//
//    static Message messageCreateTest(MessageService messageService, User author, Channel channel) {
//        Message message = messageService.createMessage("안녕하세요.", author, channel);
//        System.out.println("메시지 생성: " + message.getId());
//        return message;
//    }
//
//    static User userFindTest(UserService userService, UUID userId){
//        User user = userService.getUsersById(userId);
//        System.out.println("유저 조회 : " + user.getId());
//        return user;
//    }
//
//    static Channel channelFindTest(ChannelService channelService, UUID channelId) {
//        Channel channel = channelService.getChannelById(channelId);
//        System.out.println("채널 조회 : " + channel.getId());
//        return channel;
//    }
//
//    static Message messageFindTest(MessageService messageService, UUID messageId) {
//        Message message = messageService.getMessagesById(messageId);
//        System.out.println("메시지 조회 : " + message.getId());
//        return message;
//    }
//
//    static void userUpdateTest(UserService userService, UUID userId){
//        User user = userService.getUsersById(userId);
//        System.out.println("[수정 전 이름] : " + user.getUserName());
//        User updateUser = new User ("수정된이름",null,null,null);
//        userService.updateUser(user,updateUser);
//        System.out.println("[수정 후 이름] : " + user.getUserName());
//    }
//
//    static void channelUpdateTest(ChannelService channelService, UUID channelId) {
//        Channel channel = channelService.getChannelById(channelId);
//        System.out.println("[수정 전 이름] : " + channel.getChannelName());
//        channelService.updateChannel(channel,"수정된채널이름");
//        System.out.println("[수정 후 이름] : " + channel.getChannelName());
//    }
//
//    static void messageUpdateTest(MessageService messageService, UUID messageId){
//        Message message = messageService.getMessagesById(messageId);
//        System.out.println("[수정 전 내용] : " + message.getContent());
//        messageService.updateMessage(message,"수정된 내용입니다");
//        System.out.println("[수정 후 내용] : " + message.getContent());
//    }
//
//    static void userRemoveTest(UserService userService, UUID userId) {
//        User user = userService.getUsersById(userId);
//        System.out.println("삭제 전 조회 : " + userService.getUsersById(userId));
//        userService.deleteUser(user);
//        try {
//            System.out.println("삭제 후 조회 : " + userService.getUsersById(userId));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    static void channelRemoveTest(ChannelService channelService, UUID channelId) {
//        Channel channel = channelService.getChannelById(channelId);
//        System.out.println("삭제 전 조회 : " + channelService.getChannelById(channelId));
//        channelService.deleteChannel(channel);
//        try {
//            System.out.println("삭제 후 조회 : " + channelService.getChannelById(channelId));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    static void messageRemoveTest(MessageService messageService, UUID messageId) {
//        Message message = messageService.getMessagesById(messageId);
//        System.out.println("삭제 전 조회 : " + messageService.getMessagesById(messageId));
//        messageService.removeMessage(message);
//        try {
//            System.out.println("삭제 후 조회 : " + messageService.getMessagesById(messageId));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    static void Test(UserService userService, ChannelService channelService, MessageService messageService, UUID userId, UUID channelId, UUID messageId) {
//        User findUser = userFindTest(userService,userId);
//        System.out.println("찾은 유저 : " + findUser.getId());
//        Channel findChannel = channelFindTest(channelService, channelId);
//        System.out.println("찾은 채널 : " + findChannel.getId());
//        Message findMessage = messageFindTest(messageService,messageId);
//        System.out.println("찾은 메시지 : " + findMessage.getId());
//
//        System.out.println("유저 업데이트 테스트");
//        userUpdateTest(userService,userId);
//        System.out.println("채널 업데이트 테스트");
//        channelUpdateTest(channelService,channelId);
//        System.out.println("메시지 업데이트 테스트");
//        messageUpdateTest(messageService,messageId);
//
//        System.out.println("메시지 삭제 테스트");
//        messageRemoveTest(messageService,messageId);
//
//        System.out.println("채널 삭제 테스트");
//        channelRemoveTest(channelService,channelId);
//
//        System.out.println("유저 삭제 테스트");
//        userRemoveTest(userService,userId);
//
//    }
//    public static void main(String[] args) {
//        Factory factory = Factory.getJCFInstance();
//        UserService userService = factory.getUserService();
//        ChannelService channelService = factory.getChannelService();
//        MessageService messageService = factory.getMessageService();
//
////        MessageService basicMessageService = new BasicMessageService(factory.getMessageRepository()
////                                            , factory.getUserRepository(), factory.getChannelRepository());
////        UserService basicUserService = new BasicUserService(factory.getUserRepository()
////                                        , factory.getChannelRepository(), factory.getMessageRepository());
////        ChannelService basicChannelService = new BasicChannelService(factory.getChannelRepository()
////                                        , factory.getUserRepository(), factory.getMessageRepository());
//
//        System.out.println("[[[[[[[[[[[[[[[ 셋업 및 테스트 ]]]]]]]]]]]]]]]");
//        // 셋업
//        User setupUser = setupUser(userService);
//        Channel setupChannel = setupChannel(channelService, setupUser);
//
//        // 테스트
//        Message setupMessage = messageCreateTest(messageService, setupUser, setupChannel);
//
//        Test(userService,channelService,messageService,setupUser.getId(),setupChannel.getId(),setupMessage.getId());
//        System.out.println("[[[[[[[[[[[[[[[ 셋업 및 테스트 ]]]]]]]]]]]]]]]");
//
//        /****************************************
//         *  정상 데이터 테스트
//         ****************************************/
//
//        System.out.println("------------- 1. USER 테스트 ------------");
//        System.out.println("---------등록---------");
//        User u1 = userService.createUser("김코딩","password", "email1@abc.df");
//        User u2 = userService.createUser("이코드", "password2", "abcd@email.com");
//        User u3 = userService.createUser("박자바","pswd123", "java@email.com");
//        User u4 = userService.createUser("김코테","code1234","code@test.com");
//        System.out.println();
//
//        System.out.println("---------다건조회---------");
//        userService.getUsers().
//                forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------단건조회---------");
//        User targetUser = userService.getUsersById(u1.getId());
//        System.out.println(targetUser);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (수정 전)---------");
//        userService.getUsers().
//                forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (수정 후)---------");
//        userService.updateUser(u1, new User("최객체",null,null, PresenceStatus.AWAY));
//        userService.updateUser(u2, new User(null,"passwordChange","ChangeMail@email.com",null));
//        userService.getUsers().
//                forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (삭제 전)---------");
//        userService.getUsers().
//                forEach(System.out::println);
//        userService.deleteUser(u4);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (삭제 후)---------");
//        userService.getUsers().
//                forEach(System.out::println);
//        System.out.println();
//        System.out.println(userService.getUsers());
//
//        try {
//            System.out.println("삭제된 유저 조회");
//            System.out.println(userService.getUsersById(u4.getId()));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        /*********************************
//         * 채널 테스트
//         *********************************/
//
//        System.out.println("------------------2. Channel 테스트------------------");
//        System.out.println("---------등록---------");
//        Channel ch1 = channelService.createChannel(u1,"채널1");
//        Channel ch2 = channelService.createChannel(u2,"채널2");
//        Channel ch3 = channelService.createChannel(u1,"채널3");
//        System.out.println();
//
//        System.out.println("---------다건 조회---------");
//        channelService.getAllChannels()
//                .forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------단건 조회---------");
//        Channel targetChannel = channelService.getChannelById(ch2.getId());
//        System.out.println(targetChannel);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (수정 전)---------");
//        channelService.getAllChannels().
//                forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (수정 후)---------");
//        channelService.updateChannel(ch2,"수정된 채널");
//        channelService.getAllChannels()
//                .forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (삭제 전)---------");
//        channelService.getAllChannels()
//                .forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (삭제 후)---------");
//        channelService.deleteChannel(ch1);
//        channelService.getAllChannels()
//                .forEach(System.out::println);
//        System.out.println();
//        try {
//            System.out.println("삭제된 채널 조회");
//            System.out.println(channelService.getChannelById(ch1.getId()));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        System.out.println("--------- 채널 내 유저 추가 ---------");
//        channelService.getAllChannels()
//                .forEach(channel -> {
//                    System.out.println(channel.getChannelName());
//                    System.out.println(channel.getUserNames());
//                });
//        channelService.addUserToChannel(ch2,u3);
//        channelService.addUserToChannel(ch2,u1);
//        channelService.addUserToChannel(ch3,u3);
//        System.out.println();
//
//        System.out.println("--------- 유저 추가 후 ---------");
//        channelService.getAllChannels()
//                .forEach(channel -> System.out.println(channel.getUserNames()));
//        System.out.println();
//
//        System.out.println("--------- 채널 내 유저 삭제 ---------");
//        channelService.removeUserFromChannel(ch2,u3.getId());
//        channelService.getAllChannels()
//                .forEach(channel -> System.out.println(channel.getUserNames()));
//        System.out.println();
//
//        /**********************************************
//         * 메세지 테스트
//         **********************************************/
//
//        System.out.println("------------------3. Message 테스트------------------");
//        System.out.println("---------등록---------");
//        try {
//            Message msg1 = messageService.createMessage("내용1",u1,ch1);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        Message msg2 = messageService.createMessage("내용2",u2,ch3);
//        Message msg3 = messageService.createMessage("내용3",u1,ch2);
//        Message msg4 = messageService.createMessage("내용4",u3,ch2);
//        Message msg5 = messageService.createMessage("내용5",u2,ch2);
//        System.out.println();
//
//        System.out.println("---------다건 조회---------");
//        messageService.getMessages()
//                .forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------단건 조회---------");
//        Message targetMessage = messageService.getMessagesById(msg4.getId());
//        System.out.println(targetMessage);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (수정 전)---------");
//        messageService.getMessages().
//                forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (수정 후)---------");
//        messageService.updateMessage(msg4,"수정된 내용입니다.");
//        messageService.getMessages().
//                forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (삭제 전)---------");
//        messageService.getMessages()
//                .forEach(System.out::println);
//        System.out.println();
//
//        System.out.println("---------전체 조회 (삭제 후)---------");
//        messageService.removeMessage(msg2);
//        messageService.getMessages()
//                .forEach(System.out::println);
//
//        try {
//            System.out.println("삭제된 메시지 조회");
//            System.out.println(messageService.getMessagesById(msg2.getId()));
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        System.out.println();
//
//        /***********************************************
//         * 전체 조회
//         ***********************************************/
//        System.out.println("------------------4. 전체 조회------------------");
//        System.out.println("---------유저 조회---------");
//        userService.getUsers()
//                .forEach(System.out::println);
//
//        System.out.println("---------채널 조회---------");
//        channelService.getAllChannels()
//                .forEach(System.out::println);
//
//        System.out.println("---------메세지 조회---------");
//        messageService.getMessages()
//                .forEach(System.out::println);
//        System.out.println();
//
//        /***********************************
//         * 유저 삭제 시 채널 및 메시지 삭제 확인
//         ***********************************/
//        System.out.println("------------------ 5. 유저 삭제 시 채널 내 유저 삭제 확인 ------------------");
//
//        User testUser1 = userService.createUser("유저1","testPW1","TEST@email.com");
//        User testUser2 = userService.createUser("유저2","TESTpw2","testmail@email.com");
//        User testUser3 = userService.createUser("유저3","testPSWD","email@pass.word");
//
//        Channel testChannel = channelService.createChannel(testUser1,"테스트채널");
//        channelService.addUserToChannel(testChannel,testUser2);
//        channelService.addUserToChannel(testChannel,testUser3);
//        System.out.println(testChannel.getUserNames());
//        System.out.println("유저3 삭제");
//        userService.deleteUser(testUser3);
//        System.out.println(testChannel.getUserNames());
//        System.out.println();
//
//        /****************************************
//         *  존재하지 않는 필드 테스트용 더미 값
//         ****************************************/
//
//        System.out.println("------------------ 더미 데이터 ------------------");
//
//        UUID tempUID = UUID.randomUUID();
//        User tempUser = new User("더미","tempPW","TEMPTEMP@TEMP.TEMP");
//        Channel tempChannel = new Channel(tempUser.getId(),"더미채널");
//        Message tempMessage = new Message("더미내용",tempUser,tempChannel);
//
//        try {
//            // 실존 채널에 더미 유저 추가 ( users에 안들어가 있음 )
//            channelService.addUserToChannel(testChannel,tempUser);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        try {
//            // 각종 더미 메시지
//            messageService.createMessage("더미유저의 메시지",tempUser,tempChannel);
//            messageService.createMessage("실존 유저가 더미 채널에 메시지",testUser1,tempChannel);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        System.out.println(tempChannel.getUserNames());
//        System.out.println(testUser1.getMessageContents());
//        System.out.println(testChannel.getUserNames());
//        System.out.println();
//
//        System.out.println("----------------------메시지 삭제 확인--------------------------");
//        System.out.println(testUser2.getChannelNames());
//        channelService.removeUserFromChannel(testChannel,testUser2.getId());
//        System.out.println(testUser2.getChannelNames());
//        System.out.println("---");
//        Message m1 = messageService.createMessage("<UNK> <UNK>",testUser1,testChannel);
//        Message m2 = messageService.createMessage("<TEST> TEST CONTENT",testUser2,testChannel);
//        System.out.println(testChannel.getMessageContents());
//        channelService.removeMessage(testChannel,m1);
//        System.out.println(testChannel.getMessageContents());
//        System.out.println();
//
//        System.out.println("----------------------유저 삭제 후 채널, 채널에서 유저 삭제 후 유저가 참여중인 채널 확인-----------------------");
//        try {
//            System.out.println(testChannel.getUserNames());
//            channelService.addUserToChannel(testChannel,testUser2);
//            channelService.addUserToChannel(testChannel,testUser3);
//            userService.leaveChannel(testUser3,testChannel);
//            System.out.println(testChannel.getUserNames());
//            channelService.removeUserFromChannel(testChannel,testUser2.getId());
//            System.out.println(testChannel.getUserNames());
//            System.out.println(testUser2.getChannelNames());
//        } catch (Exception e) {
//            System.out.println("더미 데이터 테스트 중 예외 발생");
//            System.out.println(e.getMessage());
//        }
//        System.out.println();
//        System.out.println("----------------------전체 내용 REWIND----------------------");
//        System.out.println("---------------------- USER ----------------------");
//        System.out.println(userService.getUsers());
//        System.out.println("---------------------- CHANNEL ----------------------");
//        System.out.println(channelService.getAllChannels());
//        System.out.println("---------------------- MESSAGE ----------------------");
//        System.out.println(messageService.getActiveMessages());
//        System.out.println("----------------------전체 내용 REWIND----------------------");
//    }
//}