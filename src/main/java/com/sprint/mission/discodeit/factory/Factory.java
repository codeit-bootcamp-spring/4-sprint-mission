//package com.sprint.mission.discodeit.factory;
//
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.mapper.*;
//import com.sprint.mission.discodeit.repository.*;
//import com.sprint.mission.discodeit.repository.file.*;
//import com.sprint.mission.discodeit.repository.jcf.*;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//import lombok.Getter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//
//
/// *********************************************
// *  Service를 관리하는 Factory 클래스
// *  Service를 생성하고 주입시키는 역할
// *  2025.06.02 김민수
// *********************************************/
//
//@Getter
//public class Factory {
//    private static Factory instance;
//    private final UserService userService;
//    private final ChannelService channelService;
//    private final MessageService messageService;
//
//    private final UserRepository userRepository;
//    private final ChannelRepository channelRepository;
//    private final MessageRepository messageRepository;
//    private final BinaryContentRepository binaryContentRepository;
//    private final ReadStatusRepository readStatusRepository;
//    private final UserStatusRepository userStatusRepository;
//
//    private final UserMapper userMapper;
//    private final ChannelMapper channelMapper;
//    private final MessageMapper messageMapper;
//    private final BinaryContentMapper binaryContentMapper;
//    private final ReadStatusMapper readStatusMapper;
//    private final UserStatusMapper userStatusMapper;
//
////    @Bean
////    @ConditionalOnProperty(newName = "discodeit.repository.type", havingValue = "jcf")
////    @Primary
//    public static Factory getJCFInstance() {
//        if (instance == null) {
//            instance = new Factory(new JCFUserRepository(), new JCFChannelRepository(), new JCFMessageRepository(),
//                    new JCFBinaryContentRepository(), new JCFReadStatusRepository(), new JCFUserStatusRepository());
//        }
//        return instance;
//    }
////
////    @Bean
////    @ConditionalOnProperty(newName = "discodeit.repository.type", havingValue = "file")
//    public static Factory getFileInstance(){
//        if (instance == null) {
//            instance = new Factory(new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository(),
//                    new FileBinaryContentRepository(), new FileReadStatusRepository(), new FileUserStatusRepository());
//        }
//        return instance;
//    }
//
//    private Factory(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository,
//                    BinaryContentRepository binaryContentRepository, ReadStatusRepository readStatusRepository, UserStatusRepository userStatusRepository) {
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//        this.binaryContentRepository =  binaryContentRepository;
//        this.readStatusRepository = readStatusRepository;
//        this.userStatusRepository = userStatusRepository;
//
//        this.userMapper = new UserMapper();
//        this.channelMapper = new ChannelMapper();
//        this.messageMapper = new MessageMapper();
//        this.binaryContentMapper = new BinaryContentMapper();
//        this.readStatusMapper = new ReadStatusMapper();
//        this.userStatusMapper = new UserStatusMapper();
//
//        messageService = new BasicMessageService(getMessageRepository()
//                                ,getUserRepository(),getChannelRepository(),getBinaryContentRepository(),getBinaryContentMapper());
//        userService = new BasicUserService(getUserRepository()
//                                ,getChannelRepository()
//                                ,getMessageRepository(), getBinaryContentRepository(), getUserStatusRepository()
//                ,getBinaryContentMapper(), getUserMapper());  // 유저서비스는 메시지서비스를 주입받는다
//        channelService = new BasicChannelService(getChannelRepository()
//                ,getMessageRepository(), getReadStatusRepository()
//                ,getChannelMapper(), getReadStatusMapper());  // 채널서비스는 메시지서비스를 주입받는다
//    }
//
//}
