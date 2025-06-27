package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class JcfChannelRepository implements ChannelRepository {

    private List<Channel> channelData = new ArrayList<>();

    @Override
    public List<Channel> loadChannels() {
        return channelData;
    }

    @Override
    public void saveChannels(List<Channel> channels) {
        channelData = channels;
    }

    @Override
    public void createChannel(Channel channel) {
        List<Channel> channels = loadChannels();
        channels.add(channel);
        saveChannels(channels);
    }

    @Override
    public void deleteChannel(Channel channel) {
        List<Channel> channels = loadChannels();
        channels.removeIf(ch -> ch.equalsId(channel));
        saveChannels(channels);
    }

    @Override
    public void updateChannel(Channel channel) {
        List<Channel> channels = loadChannels();

        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).equalsId(channel)) {
                channels.set(i, channel);  // 리스트 내부 요소를 실제로 교체
                saveChannels(channels);   // 변경된 리스트 저장
                return;
            }
        }

        throw new IllegalArgumentException("해당 ID를 가진 채널이 존재하지 않습니다.");
    }

    @Override
    public void deleteUserFromChannels(User user){
        List<Channel> channelsFromFile = loadChannels();

        boolean hasHostChannel = channelsFromFile.stream()
                .anyMatch(channel -> channel.getHostUserId().equals(user.getId()));

        if (hasHostChannel) {
            // 채널 주인이란 의미이므로 return
            System.out.println("호스트인 채널이 있어서 유저를 삭제할 수 없습니다.");
            return;
        }

        for (Channel channelFromFile : channelsFromFile) {
            if(user.getChannelIds().contains(channelFromFile.getId())){
                channelFromFile.removeUser(user);
            }
        }
    }

    @Override
    public Optional<Channel> findChannelByChannelName(String channelName) {
        if (channelName == null || channelName.length() == 0) return Optional.empty();

        return loadChannels().stream()
                .filter(ch -> ch.getChannelName()!=null && ch.getChannelName().equals(channelName))
                .findFirst();
    }

    @Override
    public Optional<Channel> findChannelByChannelId(UUID channelId) {
        return loadChannels().stream()
                .filter(ch -> ch.equalsId(channelId))
                .findFirst();
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID userId){
        return loadChannels().stream().filter(ch -> ch.getUserIds().contains(userId)).collect(Collectors.toList());
    }
}