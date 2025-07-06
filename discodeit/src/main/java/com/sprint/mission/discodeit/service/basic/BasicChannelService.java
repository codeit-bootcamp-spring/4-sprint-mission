package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PUBLIC, dto.getName(), dto.getDescription());
        channelRepository.save(channel);
        return channelMapper.channelToChannelResponseDto(channel, null);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        List<UUID> participantUserIds = dto.getParticipantUserIds();
        for (UUID userId : participantUserIds) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.save(readStatus);
        }

        return channelMapper.channelToChannelResponseDto(
                channel, dto.getParticipantUserIds());
    }

    @Override
    public ChannelResponseDto findById(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다: " + channelId));

        Optional<Instant> lastMessageTime = messageRepository.findLastMessageTimeByChannelId(channelId);

        List<UUID> participants = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannelId(channelId).stream()
                    .map(ReadStatus::getUserId)
                    .distinct()
                    .toList();
        }

        ReadStatusDto readStatusDto = readStatusRepository.findByChannelIdAndUserId(channelId, userId).map(readStatus -> new ReadStatusDto(readStatus.getUserId(), readStatus.getChannelId()))
                .orElse(null);

        return channelMapper.channelToChannelResponseDto(channel, participants);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {

        List<Channel> allChannels = channelRepository.findAll();

        List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .distinct()
                .toList();

        return allChannels.stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC || privateChannelIds.contains(channel.getId()))
                .map(channel -> {
                    List<UUID> participantUserIds = channel.getType() == ChannelType.PRIVATE
                            ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                            .map(ReadStatus::getUserId)
                            .distinct()
                            .toList()
                            : Collections.emptyList();

                    return channelMapper.channelToChannelResponseDto(channel, participantUserIds);
                })
                .toList();
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateDto dto) {
        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("수정하려는 채널이 존재하지 않습니다: " + dto.getChannelId()));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE 채널은 수정할 수 없습니다. (id: " + channel.getId() + ")");
        }

        channel.update(dto.getNewName(), dto.getNewDescription());
        channelRepository.save(channel);

        return channelMapper.channelToChannelResponseDto(channel, null);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("삭제하려는 채널이 존재하지 않습니다: " + channelId));

        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);

        channelRepository.deleteById(channel.getId());
    }
}
