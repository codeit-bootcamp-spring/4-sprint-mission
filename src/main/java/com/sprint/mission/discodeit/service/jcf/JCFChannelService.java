package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.utility.ChannelValidator;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private static final JCFChannelRepository jcfChannelRepository = new JCFChannelRepository();

    // Create
    @Override
    public Channel createChannel(String name, User hostUser) {
        if (!DetectUtility.detect(name, hostUser)) {
            throw new RuntimeException("Invalid channel name or host user");
        }
        Channel channel = new Channel(name, hostUser.getId());
        jcfChannelRepository.addChannelAndSave(channel, hostUser);
        return channel;
    }

    // Read
    @Override
    public List<Channel> getChannels() {
        return jcfChannelRepository.getAll();
    }

    @Override
    public Channel getChannelById(UUID id) {
        return jcfChannelRepository.getById(id);
    }

    // Update
    @Override
    public void updateChannelNameByChannel(Channel channel, String name) {
        if (!DetectUtility.detect(channel) || !DetectUtility.detect(name)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfChannelRepository.updateChannelName(channel, name);
    }

    // Delete
    @Override
    public void deleteChannelByChannelAndHostUser(Channel channel, User hostUser) {
        if (!ChannelValidator.detectChannelDelete(channel, hostUser)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        jcfChannelRepository.delete(channel, hostUser);
    }

    @Override
    public void deleteAllChannels() {
        jcfChannelRepository.deleteAll();
    }
}
