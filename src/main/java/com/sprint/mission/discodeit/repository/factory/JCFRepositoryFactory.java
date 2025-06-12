package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;

public class JCFRepositoryFactory implements RepositoryFactory {
    @Override
    public UserRepository CreateUserRepository() {
        return JCFUserRepository.getInstance();
    }
    public ChannelRepository CreateChannelRepository() {
        return JCFChannelRepository.getInstance();
    }
    public MessageRepository CreateMessageRepository() {
        return JCFMessageRepository.getInstance();
    }
}
