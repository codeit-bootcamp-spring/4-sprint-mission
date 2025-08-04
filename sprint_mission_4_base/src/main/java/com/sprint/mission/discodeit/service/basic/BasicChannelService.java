package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.maper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto create(PublicChannelCreateRequest request) {
        Channel channel = channelMapper.publicCreateRequestToChannel(request);
        channelRepository.save(channel);

        return toDto(channel);
    }

    @Override
    public ChannelResponseDto create(PrivateChannelCreateRequest request) {
        Channel channel = channelMapper.privateCreateRequestToChannel();
        channelRepository.save(channel);

        request.participantIds().stream()
                .map(userId -> new ReadStatus(userId, channel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return toDto(channel);
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || mySubscribedChannelIds.contains(channel.getId())
                )
                .map(this::toDto)
                .toList();
    }

    @Override
    public ChannelResponseDto update(UUID channelId, PublicChannelUpdateRequest request) {
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("Private channel cannot be updated");
        }

        channel.update(newName, newDescription);
        channelRepository.save(channel);

        return toDto(channel);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        messageRepository.deleteAllByChannelId(channel.getId());
        readStatusRepository.deleteAllByChannelId(channel.getId());

        channelRepository.deleteById(channelId);
    }

    private ChannelResponseDto toDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .map(Message::getCreatedAt)
                .limit(1)
                .findFirst()
                .orElse(Instant.MIN);

        List<UUID> participantIds = new ArrayList<>();
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUserId)
                    .forEach(participantIds::add);
        }

        return channelMapper.channelToResponseDto(channel, participantIds, lastMessageAt);
    }
}
