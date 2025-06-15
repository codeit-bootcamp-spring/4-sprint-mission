package com.sprint.mission.discodeit.repository.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

public interface RepositoryFactory {
    UserRepository CreateUserRepository();
    MessageRepository CreateMessageRepository();
    ChannelRepository CreateChannelRepository();
}
