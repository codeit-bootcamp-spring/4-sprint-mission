package com.sprint.mission.discodeit.service.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.JCF.JCFChannelService;
import com.sprint.mission.discodeit.service.JCF.JCFMessageService;
import com.sprint.mission.discodeit.service.JCF.JCFUserService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFServiceFactory implements ServiceFactory {
    @Override
    public UserService createUserService() {
        return JCFUserService.getInstance();
    }
    @Override
    public MessageService createMessageService() {
        return JCFMessageService.getInstance();
    }
    @Override
    public ChannelService createChannelService() {
        return JCFChannelService.getInstance();
    }
}
