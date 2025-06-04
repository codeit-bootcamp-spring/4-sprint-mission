package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
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

    private final Map<String, Channel> channels = new HashMap<>();
    private final UserService userService = JCFUserService.getInstance();

    @Override
    public void addChannel(Channel channel) {
        channels.put(channel.getChannelId().toString(), channel);
    }

    @Override
    public List<Channel> getChannels() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void updateChannel(UUID channelId, int selectedNum, String updatedText) {
      Channel channel = channels.get(channelId.toString());
      if (selectedNum ==1) {
          channel.setChannelName(updatedText);
          channel.setUpdatedAt(System.currentTimeMillis());
        }
    }

    @Override
    public void deleteChannel(UUID channelId) {
        channels.remove(channelId.toString());
    }

    /**유저가 채널에 참여할 수 있도록 채널에 유저를 추가합니다.
     *유효한 채널과 유저인지 먼저 확인*/
    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = channels.get(channelId.toString());
        User user = userService.getUserById(userId);
        if (channel != null && user != null) {
            channel.addJoinedUser(user);
        }
    }

    /**유저가 채널에서 퇴장할 수 있도록 채널에서 유저를 삭제합니다.
     *유효한 채널과 유저인지 먼저 확인*/
    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channels.get(channelId.toString());
        //유저가 채널에서 나갈 수 있도록 채널에서 유저를 제외
        //유효한 채널과 유저인지 먼저 확인
        User user = userService.getUserById(userId);
        if (channel != null && user != null) {
            channel.removeJoinedUser(user);
        }
    }

    @Override
    public void findChannelByKeyword(String keyword) {
        System.out.println("[" + keyword + "] 키워드로 검색한 결과: ");
        for (Channel channel : channels.values()) {
            if(channel.getChannelName().toLowerCase().contains(keyword)){
                System.out.println(channel);
            }
        }
    }
}