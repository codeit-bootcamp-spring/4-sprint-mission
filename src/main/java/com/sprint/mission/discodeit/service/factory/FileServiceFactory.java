package com.sprint.mission.discodeit.service.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class FileServiceFactory implements ServiceFactory{
    @Override
    public UserService createUserService() {
        return FileUserService.getInstance();
    }
    @Override
    public MessageService createMessageService() {
        return FileMessageService.getInstance();
    }
    @Override
    public ChannelService createChannelService() {
        return FileChannelService.getInstance();
    }
}
