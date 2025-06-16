package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class ServiceFactory {
    private static ServiceFactory instance;
    private UserService userService;
    private ChannelService channelService;
    private MessageService messageService;

    private FileUserService fileUserService;
    private FileChannelService fileChannelService;
    private FileMessageService fileMessageService;

    private BasicUserService basicUserService;
    private BasicChannelService basicChannelService;
    private BasicMessageService basicMessageService;

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    private ServiceFactory() {
        /*
        userService = JCFUserService.getInstance();
        channelService = JCFChannelService.getInstance();
        messageService = JCFMessageService.getInstance();

        fileUserService = FileUserService.getInstance();
        fileChannelService = FileChannelService.getInstance();
        fileMessageService = FileMessageService.getInstance();*/ //sprint2에서는 안 쓰는 코드
    }

    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public FileUserService getFileUserService() {
        return fileUserService;
    }
    public FileChannelService getFileChannelService() {
        return fileChannelService;
    }
    public FileMessageService getFileMessageService() {
        return fileMessageService;
    }

    public BasicUserService getBasicUserService(UserRepository userRepository) {
        return new BasicUserService(userRepository);
    }
    public BasicChannelService getBasicChannelService(ChannelRepository channelRepository) {
        return new BasicChannelService(channelRepository);
    }
    public BasicMessageService getBasicMessageService(MessageRepository messageRepository) {
        return new BasicMessageService(messageRepository);
    }
}
