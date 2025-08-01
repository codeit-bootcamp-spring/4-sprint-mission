package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    HashMap<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel createChannel(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> getChannels() {
       return new ArrayList<>(data.values());
    }

    @Override
    public void updateChannel(UUID name, String Channel) {
        Channel channel = data.get(name);
        if(name != null){
            channel.updateChannel(Channel);
        }
    }

    @Override
    public boolean deleteChannel(UUID name) {
        if(!data.containsKey(name)){
            System.out.println("삭제할 채널이 존재하지 않습니다.");
            return false;
        }
        data.remove(name);
        return true;
    }
}
