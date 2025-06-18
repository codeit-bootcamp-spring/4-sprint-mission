package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    /** 싱글톤 패턴을 적용하여 JCFChannelService의 단일 인스턴스만 생성되도록 합니다*/
    private static final JCFChannelService instance = new JCFChannelService();
    private JCFChannelService() {}
    public static JCFChannelService getInstance() {
        return instance;
    }

    private final UserService userService = JCFUserService.getInstance();
    private final UserRepository userRepository = JCFUserRepository.getInstance();

    //private final Map<String, Channel> channels = new HashMap<>();
    private final ChannelRepository channelRepository = JCFChannelRepository.getInstance();

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
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);
        optionalChannel.ifPresent(channel -> {
            if (selectedNum == 1) {
                channel.setChannelName(updatedText);
                channel.setUpdatedAt(System.currentTimeMillis());
                channelRepository.save(channel);
            }
        });
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channelRepository.deleteById(channelId);
    }

    /**유저가 채널에 참여할 수 있도록 채널에 유저를 추가합니다.
     *유효한 채널과 유저인지 먼저 확인*/
    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalChannel.isPresent() && optionalUser.isPresent()) {
            Channel channel = optionalChannel.get();
            User user = optionalUser.get();
            channel.addJoinedUser(user);
            channelRepository.save(channel);
        }
    }

    /**유저가 채널에서 퇴장할 수 있도록 채널에서 유저를 삭제합니다.
     *유효한 채널과 유저인지 먼저 확인*/
    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Optional<Channel> optionalChannel = channelRepository.findById(channelId);
        //유저가 채널에서 나갈 수 있도록 채널에서 유저를 제외
        //유효한 채널과 유저인지 먼저 확인
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalChannel.isPresent() && optionalUser.isPresent()) {
            Channel channel = optionalChannel.get();
            User user = optionalUser.get();
            channel.removeJoinedUser(user);
            channelRepository.save(channel);
        }
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