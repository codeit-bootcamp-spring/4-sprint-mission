package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.SseMessageEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final ChannelMapper channelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(value = "channels", allEntries = true)
    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        log.debug("공개 채널 생성 호출");
        validateDuplicatedChannelName(publicChannelCreateRequest.name());
        Channel channel = new Channel(publicChannelCreateRequest.name(), publicChannelCreateRequest.description());
        channelRepository.save(channel);
        log.info("공개 채널 생성 완료 id = {}", channel.getId());
        eventPublisher.publishEvent(new SseMessageEvent(
                "channels.created",
                channelMapper.toChannelDto(channel)
        ));
        return channel;
    }

    @Transactional
    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        log.debug("비공개 채널 생성 호출");

        Channel channel = new Channel();
        channelRepository.save(channel);

        for (UUID id : privateChannelCreateRequest.participantIds()) {
            User user = getValidUser(id);
            cacheManager.getCache("channels").evict(id);
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }
        log.info("비공개 채널 생성 완료 id = {}", channel.getId());
        eventPublisher.publishEvent(new SseMessageEvent(
                "channels.created",
                channelMapper.toChannelDto(channel)
        ));
        return channel;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "channels", key = "#userId")
    public List<Channel> findAllByUserId(UUID userId) {
        getValidUser(userId);
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUser_Id(userId);
        Set<Channel> channels = new LinkedHashSet<>();
        readStatuses.forEach(readStatus -> {
            channels.add(readStatus.getChannel());
        });
        channels.addAll(channelRepository.findAllByType(ChannelType.PUBLIC));
        return new ArrayList<>(channels);
    }

    @Transactional
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(value = "channels", allEntries = true)
    @Override
    public Channel updateChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        log.debug("공개 채널 수정 호출 id = {}", channelId);
        Channel findChannel = getValidChannel(channelId);
        validatePublicChannel(findChannel);

        Optional.ofNullable(publicChannelUpdateRequest.newName())
                .ifPresent(findChannel::setName);
        Optional.ofNullable(publicChannelUpdateRequest.newDescription())
                .ifPresent(findChannel::setDescription);

        channelRepository.save(findChannel);

        log.info("공개 채널 수정 완료 id = {}", channelId);
        eventPublisher.publishEvent(new SseMessageEvent(
                "channels.updated",
                channelMapper.toChannelDto(findChannel)
        ));
        return findChannel;
    }

    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @CacheEvict(value = "channels", allEntries = true)
    @Override
    public void deleteChannel(UUID id) {
        log.debug("채널 삭제 호출 id = {}", id);
        Channel channel = getValidChannel(id);
        log.info("채널 삭제 완료 id = {}", id);
        eventPublisher.publishEvent(new SseMessageEvent(
                "channels.deleted",
                channelMapper.toChannelDto(channel)
        ));
        channelRepository.delete(channel);
    }

    private Channel getValidChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("해당 채널을 찾을 수 없음 id = {}", id);
                    throw new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND, Map.of("channelId", id));
                });
    }

    private void validatePublicChannel(Channel channel) {
        if (channel.getType().equals(ChannelType.PRIVATE)) {
            log.warn("비공개 채널은 수정할 수 없음 id = {}, type = {}", channel.getId(), channel.getType());
            throw new PrivateChannelUpdateException(ErrorCode.PRIVATE_CHANNEL_CANNOT_UPDATE, Map.of("channelId", channel.getId()));
        }
    }

    private User getValidUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("해당 유저가 존재하지 않음 id = {}", userId);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
        });
    }

    private void validateDuplicatedChannelName(String channelName) {
        if (channelRepository.existsByName(channelName)) {
            log.warn("채널이름 중복 name = {}", channelName);
            throw new ChannelAlreadyExistsException(ErrorCode.AlREADY_EXISTS_CHANNEL_NAME, Map.of("channelName", channelName));
        }
    }
}
