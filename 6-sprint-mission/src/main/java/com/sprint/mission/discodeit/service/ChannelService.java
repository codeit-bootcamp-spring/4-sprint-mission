package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

    public ChannelDto createPublicChannel(
            PublicChannelCreateRequest publicChannelCreateRequest); // PUBLIC 채널 생성

    public ChannelDto createPrivateChannel(List<UUID> users); // PRIVATE 채널 생성

    public List<ChannelDto> findAllByUserId(UUID id); // 모든 채널 출력

    public ChannelDto findChannelById(UUID channelId); // 특정 채널 출력

    public List<ChannelDto> findChannelsByNameContains(String channelName); // 특정 이름이 포함되어있는 채널 출력

    public List<MessageDto> findMessagesByUserInChannel(
            UUID channelId, UUID userId); // 채널에서 특정 유저가 쓴 메세지 확인

    public ChannelDto updateChannelName(
            PublicChannelUpdateRequest publicChannelUpdateRequest); // 채널 이름 업데이트

    public void joinUser(UUID channelId, UUID userId); // 채널에 유저 초대

    public void leaveUser(UUID channelId, UUID userId); // 채널에서 유저 추방

    public void deleteChannel(UUID id); // 채널 삭제
}
