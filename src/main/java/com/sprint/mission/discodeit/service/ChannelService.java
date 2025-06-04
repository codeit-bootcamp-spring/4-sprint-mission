package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel registChannel(String channelName);
    //신규 채널 등록

    boolean deleteChannel(UUID channelId);
    //채널 삭제

    void showChannelInfo(Channel channel);
    //선택한 채널 정보 출력

    Channel findChannelById(UUID channelId);
    //channelId로 채널 반환

    List<Channel> findChannelByName(String channelName);
    //이름이 channelName인 모든 채널 리스트 반환

    Channel updateChannelName(UUID channelId, String updateChannelName);
    //채널 이름 수정

    List<User> getUserList(UUID channelId);
    //채널 내 모든 유저 리스트 조회

    boolean KickUser(UUID channelId, User user);
    //채널 내 유저 강퇴

    List<Message> getMessageList(UUID channelId);
    //채널 내 모든 메세지 기록 조회
}
