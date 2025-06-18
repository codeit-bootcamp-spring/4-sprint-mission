package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.NoSuchElementException;

import java.util.*;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public FileChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addChannel(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public List<Channel> getChannels() {
        return new ArrayList<>(channelRepository.findAll());
    }

    @Override
    public void updateChannel(UUID channelId, int selectedNum, String updatedText) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));

            if (selectedNum == 1) {
                channel.setChannelName(updatedText);
                channel.setUpdatedAt(System.currentTimeMillis());
                channelRepository.save(channel);
            }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }
    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
            channel.addJoinedUser(user);
            channelRepository.save(channel);
        }
        
    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
          Channel channel = channelRepository.findById(channelId)
                  .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다."));
          User user = userRepository.findById(userId)
                  .orElseThrow(()-> new NoSuchElementException("존재하지 않는 유저입니다."));
          channel.removeJoinedUser(user);
          channelRepository.save(channel);
    }

    @Override
    public void printChannelByKeyword(String keyword) {
        System.out.println("[" + keyword + "] 키워드로 검색한 결과: ");
        for (Channel channel : channelRepository.findAll()) {
            if(channel.getChannelName().toLowerCase().contains(keyword)){
                System.out.println(channel);
            }
        }
    }
}
