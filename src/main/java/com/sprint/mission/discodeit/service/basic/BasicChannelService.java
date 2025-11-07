package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.ChannelUpdateEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    //
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelMapper channelMapper;
    private final ApplicationEventPublisher publisher;

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "channelCache", key = "#result.id()")},
            evict = {@CacheEvict(value = "channelCacheByUserId", allEntries = true)}
    )
    public ChannelDto create(PublicChannelCreateRequest request) {
        log.debug("public 채널 생성 시작: {}", request);
        String name = request.name();
        String description = request.description();
        Channel channel = new Channel(ChannelType.PUBLIC, name, description);

        Channel saved = channelRepository.save(channel);
        ChannelDto channelDto = channelMapper.toDto(saved);
        log.info("public 채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());

        log.debug("# 채널 갱신 SSE 이벤트 전송(created public channel), payload={}", channelDto);
        publisher.publishEvent(new ChannelUpdateEvent(channelDto, "created"));

        return channelMapper.toDto(channel);
    }

    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "channelCache", key = "#result.id()")},
            evict = {@CacheEvict(value = "channelCacheByUserId", allEntries = true)}
    )
    public ChannelDto create(PrivateChannelCreateRequest request) {
        log.debug("private 채널 생성 시작: {}", request);
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel saved = channelRepository.save(channel);
        ChannelDto channelDto = channelMapper.toDto(saved);

        List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
                .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
                .toList();
        readStatusRepository.saveAll(readStatuses);

        log.info("private 채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());

        log.debug("# 채널 갱신 SSE 이벤트 전송(created private channel), payload={}", channelDto);
        publisher.publishEvent(new ChannelUpdateEvent(channelDto, "created"));

        return channelMapper.toDto(channel);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "channelCache", key = "#channelId")
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(channelMapper::toDto)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(value = "channelCacheByUserId", key = "#userId")
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .map(Channel::getId)
                .toList();

        return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
                .stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    @Override
    @Caching(
            put = {@CachePut(value = "channelCache", key = "#channelId", unless = "#result ==null")},
            evict = {@CacheEvict(value = "channelCacheByUserId", allEntries = true)}
    )
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("채널 수정 시작: id={}, request={}", channelId, request);
        String newName = request.newName();
        String newDescription = request.newDescription();
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw PrivateChannelUpdateException.forChannel(channelId);
        }
        channel.update(newName, newDescription);
        log.info("채널 수정 완료: id={}, name={}", channelId, channel.getName());

        ChannelDto channelDto = channelMapper.toDto(channel);
        log.debug("# 채널 갱신 SSE 이벤트 전송(updated), payload={}", channelDto);
        publisher.publishEvent(new ChannelUpdateEvent(channelDto, "updated"));

        return channelMapper.toDto(channel);
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "channelCache", key = "#channelId"),
            @CacheEvict(value = "channelCacheByUserId", allEntries = true)
    })
    public void delete(UUID channelId) {
        log.debug("채널 삭제 시작: id={}", channelId);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
        ChannelDto channelDto = channelMapper.toDto(channel);

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);

        channelRepository.deleteById(channelId);
        log.info("채널 삭제 완료: id={}", channelId);

        log.debug("# 채널 갱신 SSE 이벤트 전송(deleted), payload={}", channelDto);
        publisher.publishEvent(new ChannelUpdateEvent(channelDto, "deleted"));
    }
}
