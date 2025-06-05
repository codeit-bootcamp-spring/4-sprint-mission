package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data = new ArrayList<>();

    // 등록
    @Override
    public Channel createChannel(String name) {
        Channel channel = new Channel(name);
        data.add(channel);
        return channel;
    }

    // 단건 조회
    /*@Override
    public Channel findChannelById(UUID id) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }*/
    // Stream 변경
    @Override
    public Channel findChannelById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 다건 조회
    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(data);
    }

    // 수정
    @Override
    public Channel updateChannelById(UUID id, String newName) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                channel.setChannelName(newName);
                channel.setUpdatedAt();
                return channel;
            }
        }
        return null;
    }

    // 삭제
    @Override
    public Channel deleteChannelById(UUID id) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                for (User member : channel.getMembers()) {
                    member.getChannels().remove(channel);
                }
                data.remove(channel);
                return channel;
            }
        }
        return null;
    }
}
