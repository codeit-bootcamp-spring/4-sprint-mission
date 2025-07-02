package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.util.UpdateUtil.updateIfChanged;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelCreateDto dto) {
        Channel channel = channelMapper.toEntity(dto);
        channelRepository.save(channel);

        Instant lastMessage = getLastMessageAtOrNull(channel.getId());

        return channelMapper.toDto(channel, lastMessage, null);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto) {
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        for (UUID userId : dto.getUserIds()) {
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.save(readStatus);
        }

        Instant lastMessage = getLastMessageAtOrNull(channel.getId());

        return channelMapper.toDto(channel, lastMessage, dto.getUserIds());
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);
        Instant lastMessage = getLastMessageAtOrNull(channelId);

        List<UUID> users = getUserIds(channel);

        return channelMapper.toDto(channel, lastMessage, users);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAll();
        List<ChannelResponseDto> channelDtos = new ArrayList<>();

        for (Channel channel : channels) {
            Instant lastMessage = getLastMessageAtOrNull(channel.getId());

            if (channel.getType() == ChannelType.PUBLIC) {
                channelDtos.add(channelMapper.toDto(channel, lastMessage, null));
                continue;
            }

            List<UUID> userIds = getUserIds(channel);

            if (userIds.contains(userId)) {
                channelDtos.add(channelMapper.toDto(channel, lastMessage, userIds));
            }
        }

        return channelDtos;
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateDto channelUpdateDto) {
        Channel channel = getChannelOrThrow(channelUpdateDto.getId());
        boolean anyValueUpdated = false;

        if (channel.getType() == ChannelType.PUBLIC) {
            anyValueUpdated = updateIfChanged(channelUpdateDto.getName(), channel.getName(), channel::setName, anyValueUpdated);
            anyValueUpdated = updateIfChanged(channelUpdateDto.getDescription(), channel.getDescription(), channel::setDescription, anyValueUpdated);

            if (anyValueUpdated) {
                channel.setUpdatedAt(Instant.now());
            }
        }

        Channel saved = channelRepository.save(channel);

        Instant lastMessage = getLastMessageAtOrNull(saved.getId());

        List<UUID> userIds = getUserIds(channel);

        return channelMapper.toDto(channel, lastMessage, userIds);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }

        channelRepository.deleteById(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        messageRepository.deleteByChannelId(channelId);
    }

    // 중복 제거용 메서드
    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    private Instant getLastMessageAtOrNull(UUID channelId) {
        return messageRepository.findLastUpdatedAtByChannelId(channelId).orElse(null);
    }

    private List<UUID> getUserIds(Channel channel) {
        List<UUID> userIds = List.of();
        if (channel.getType() == ChannelType.PRIVATE) {
            userIds = readStatusRepository
                    .findByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }

        return userIds;
    }
}
