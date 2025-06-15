package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.utility.ChannelValidator;
import com.sprint.mission.discodeit.service.utility.DetectUtility;
import com.sprint.mission.discodeit.service.utility.ErrorMessageUtility;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String name, User hostUser) {
        if (!DetectUtility.detect(name, hostUser)) {
            throw new RuntimeException("<실패: 채널 생성에 실패하였습니다.>");
        }
        Channel channel = new Channel(name, hostUser.getId());
        channelRepository.addChannelAndSave(channel,hostUser);
        return channel;
    }

    // Read
    @Override
    public List<Channel> getChannels() {
        return channelRepository.getAll();
    }

    @Override
    public Channel getChannelById(UUID id) {
        return channelRepository.getById(id);
    }

    // Update
    @Override
    public void updateChannelNameByChannel(Channel channel, String name) {
        if (!DetectUtility.detect(channel) || !DetectUtility.detect(name)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        channelRepository.updateChannelName(channel, name);
    }

    // Delete
    @Override
    public void deleteChannelByChannelAndHostUser(Channel channel, User hostUser) {
        if (!ChannelValidator.detectChannelDelete(channel, hostUser)) {
            ErrorMessageUtility.printErrorMessage();
            return;
        }
        channelRepository.delete(channel, hostUser);
    }

    @Override
    public void deleteAllChannels() {
        channelRepository.deleteAll();
    }

}
