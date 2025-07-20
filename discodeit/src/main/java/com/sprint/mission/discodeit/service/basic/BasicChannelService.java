package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponse createPublicChannel(PublicChannelCreateRequest dto) {

        Channel channel = new Channel(ChannelType.PUBLIC, dto.name(), dto.description());

        return channelMapper.toChannelResponse(channelRepository.save(channel));
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest dto) {
        Channel channel = channelMapper.toEntity(dto);
        channelRepository.save(channel);

        List<UUID> participantIds = dto.participantIds();
        for (UUID userId : participantIds) {
            ReadStatus readStatus = new ReadStatus(
                    UUID.randomUUID(),     // ID 명시적으로 부여
                    userId,
                    channel.getId(),
                    Instant.now()
            );
            readStatusRepository.save(readStatus);
        }

        return channelMapper.toChannelResponse(channel);
    }

    /*@Override
    public ChannelResponse findById(UUID channelId, UUID userId) {
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

        return channelMapper.toChannelResponse(channel, participants);
    }*/

    @Override
    public List<UserChannelResponse> findAllByUserId(UUID userId) {

        List<Channel> allChannels = channelRepository.findAll();

        List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return allChannels.stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC || privateChannelIds.contains(channel.getId()))
                .map(channel -> {
                    List<UUID> participantUserIds;

                    if (channel.getType() == ChannelType.PRIVATE) {
                        participantUserIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                                .map(ReadStatus::getUserId)
                                .distinct()
                                .toList();
                    } else {
                        participantUserIds = userRepository.findAll().stream()
                                .map(User::getId)
                                .toList();
                    }

                    // 마지막 메시지 시각 (임시 null 또는 메시지 테이블 연동 시 조회)
                    Instant lastMessageAt = null;

                    return channelMapper.toUserChannelResponse(channel, participantUserIds, lastMessageAt);
                })
                .toList();
    }

    @Override
    public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest dto) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("수정하려는 채널이 존재하지 않습니다: " + channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("PRIVATE 채널은 수정할 수 없습니다. (id: " + channel.getId() + ")");
        }

        channel.update(dto.newName(), dto.newDescription());

        Channel updated = channelRepository.save(channel);

        return channelMapper.toChannelResponse(updated);
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
