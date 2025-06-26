package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public ChannelResponseDto createPublicChannel(ChannelCreateDto channelCreateDto); // PUBLIC 채널 생성

    public ChannelResponseDto createPrivateChannel(List<UserResponseDto> users); // PRIVATE 채널 생성

    public List<ChannelResponseDto> findAllByUserId(UUID id); // 모든 채널 출력

    public ChannelResponseDto findChannelById(UUID channelId); // 특정 채널 출력

    public List<ChannelResponseDto> findChannelsByNameContains(
            String channelName); // 특정 이름이 포함되어있는 채널 출력

    public List<MessageResponseDto> findMessagesByUserInChannel(
            UUID channelId, UUID userId); // 채널에서 특정 유저가 쓴 메세지 확인

    public ChannelResponseDto updateChannelName(ChannelUpdateDto channelUpdateDto); // 채널 이름 업데이트

    public void joinUser(UUID channelId, UUID userId); // 채널에 유저 초대

    public void leaveUser(UUID channelId, UUID userId); // 채널에서 유저 추방

    public void deleteChannel(UUID id); // 채널 삭제
}
