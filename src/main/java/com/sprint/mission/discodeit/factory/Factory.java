package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

/*********************************************
 *  Service를 관리하는 Factory 클래스
 *  Service를 생성하고 주입시키는 역할
 *  2025.06.02 김민수
 *********************************************/
public class Factory {
    private static Factory instance;
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }
    private Factory() {
        messageService = new JCFMessageService();
        userService = new JCFUserService(getMessageService());  // 유저서비스는 메시지서비스를 주입받는다
        channelService = new JCFChannelService(getMessageService());  // 채널서비스는 메시지서비스를 주입받는다
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
