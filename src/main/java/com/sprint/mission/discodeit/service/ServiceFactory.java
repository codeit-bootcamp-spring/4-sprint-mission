package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.ServiceType;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

// 서비스 구현체를 생성해주는 팩토리 클래스
public class ServiceFactory {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public ServiceFactory(ServiceType.ServiceStatus serviceStatus) {
        switch (serviceStatus) {
            case FILE -> {
                userService = new FileUserService();
                channelService = new FileChannelService();
                messageService = new FileMessageService();
            }
            case JCF -> {
                userService = new JCFUserService();
                channelService = new JCFChannelService();
                messageService = new JCFMessageService();
            }
            default -> {
                throw new RuntimeException("Unknown service type: " + serviceStatus);
            }
        }
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
}
