package com.sprint.mission.discodeit.factory;


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


public class ServiceFactory {
    private static final UserRepository fileUserRepository = new FileUserRepository();
    private static final ChannelRepository fileChannelRepository = new FileChannelRepository();
    private static final MessageRepository fileMessageRepository = new FileMessageRepository();

    private static final UserRepository jcfUserRepository = new JCFUserRepository();
    private static final ChannelRepository jcfChannelRepository = new JCFChannelRepository();
    private static final MessageRepository jcfMessageRepository = new JCFMessageRepository();


    public static UserService createFileUserService() {
        return new BasicUserService(fileUserRepository);
    }

    public static MessageService createFileMessageService() {
        return new BasicMessageService(fileMessageRepository, fileUserRepository);
    }

    public static ChannelService createFileChannelService() {
        return new BasicChannelService(fileChannelRepository, fileUserRepository);
    }


    public static UserService createJCFUserService() {
        return new BasicUserService(jcfUserRepository);
    }

    public static MessageService createJCFMessageService() {
        return new BasicMessageService(jcfMessageRepository, jcfUserRepository);
    }

    public static ChannelService createJCFChannelService() {
        return new BasicChannelService(jcfChannelRepository, jcfUserRepository);
    }
}
