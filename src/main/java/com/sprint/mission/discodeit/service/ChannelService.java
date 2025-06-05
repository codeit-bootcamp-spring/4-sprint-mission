package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    // 등록
    Channel createChannel(String name);

    // 조회
    Channel findChannelById(UUID id);
    List<Channel> findAllChannels();

    // 수정
    Channel updateChannelById(UUID id, String newName);

    // 삭제
    Channel deleteChannelById(UUID id);
}
