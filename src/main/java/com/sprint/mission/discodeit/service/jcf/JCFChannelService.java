package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Service.ChannelService;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {
    private final Map<UUID, ChannelDto> channelList;

    public JCFChannelService() {
        channelList = new HashMap<>();
    }

    @Override
    public ChannelDto createPublic(PublicChannelRequest publicChannelRequest) {
        Channel channel = new Channel();
        channelList.put(channel.getId(),channel);
        return channel;
    }

    @Override
    public ChannelDto createPrivate(PrivateChannelRequest privateChannelRequest) {
        Channel channel = new Channel(privateChannelRequest);
        channelList.put(channel.getId(),channel);
        return channel;
    }

    @Override
    public ChannelDto findChannel(UUID id) {
        Channel findChannel = null;
        if(channelList.containsKey(id)) {
            findChannel = channelList.get(id);
        }
        return findChannel;
    }

    @Override
    List<ChannelDto> findAllChannels() {
        return channelList.values().stream().toList();
    }

    @Override
    public Channel updateChannel(UUID id, String newName) {
        Channel updatedChannel = null;
        if(newName == null && newName.isEmpty()) {
            throw new NoSuchElementException("수정할 수 없습니다.");
        } else {
            updatedChannel = channelList.get(id);
            updatedChannel.setChannelName(newName);
        }
        return updatedChannel;
    }

    @Override
    public ChannelDto updatePublicChannel(UUID id, PublicChannelRequest publicChannelRequest) {

        return null;
    }

    @Override
    public void deleteChannel(UUID id) {
        if(!channelList.containsKey(id)) {
            throw new NoSuchElementException("채널을 삭제할 수 없어요.");
        } else {
            channelList.remove(id);
        }
    }
}
