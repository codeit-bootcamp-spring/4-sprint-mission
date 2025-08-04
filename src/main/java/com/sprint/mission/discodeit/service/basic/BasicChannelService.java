package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = new Channel(publicChannelCreateRequest.name(), publicChannelCreateRequest.description());
        channelRepository.save(channel);

        return channel;
    }

    @Transactional
    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        for (UUID id : privateChannelCreateRequest.participantIds()) {
            validateUser(id);
        }

        Channel channel = new Channel();
        channelRepository.save(channel);

        for (UUID id : privateChannelCreateRequest.participantIds()) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }
        return channel;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Channel> findAllByUserId(UUID userId) {
        validateUser(userId);
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUser_Id(userId);
        Set<Channel> channels = new LinkedHashSet<>();
        readStatuses.forEach(readStatus -> {
            channels.add(readStatus.getChannel());
        });
        channels.addAll(channelRepository.findAllByType(ChannelType.PUBLIC));
        return new ArrayList<>(channels);
    }

    @Transactional
    @Override
    public Channel updateChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        validateActiveChannel(channelId);
        validatePublicChannel(channelId);

        Channel findChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
        Optional.ofNullable(publicChannelUpdateRequest.newName())
                .ifPresent(findChannel::setName);
        Optional.ofNullable(publicChannelUpdateRequest.newDescription())
                .ifPresent(findChannel::setDescription);

        channelRepository.save(findChannel);
        return findChannel;
    }

    @Override
    public void deleteChannel(UUID id) {
        validateActiveChannel(id);
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
        channelRepository.delete(channel);
    }

    private void validateActiveChannel(UUID id) {
        if (!channelRepository.existsById(id)) {
            throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND);
        }
    }

    private void validatePublicChannel(UUID id) {
        if (channelRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND))
                .getType().equals(ChannelType.PRIVATE))
            throw new BusinessLogicException(ExceptionCode.PRIVATE_CHANNEL_CANNOT_UPDATE);
    }

    private void validateUser(UUID userId) {
        if (!userRepository.existsById(userId)) throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }
}
