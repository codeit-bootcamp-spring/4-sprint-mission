package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JCFServiceFactory {

    public UserService createUserService() {
        return new JCFUserService();
    }

    public ChannelService createChannelService() {
        return new JCFChannelService();
    }

    public MessageService createMessageService() {
        return new JCFMessageService();
    }
}
