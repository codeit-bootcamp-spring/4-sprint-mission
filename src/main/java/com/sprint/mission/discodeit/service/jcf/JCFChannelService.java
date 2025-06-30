package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelList;

    public JCFChannelService() {
        channelList = new HashMap<>();
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelList.put(channel.getId(),channel);
        return channel;
    }

    @Override
    public Channel searchChannel(UUID id) {
        Channel findChannel = null;
        if(channelList.containsKey(id)) {
            findChannel = channelList.get(id);
        }
        return findChannel;
    }

    @Override
    public List<Channel> searchAll() {
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
    public void deleteChannel(UUID id) {
        if(!channelList.containsKey(id)) {
            throw new NoSuchElementException("채널을 삭제할 수 없어요.");
        } else {
            channelList.remove(id);
        }
    }
}
