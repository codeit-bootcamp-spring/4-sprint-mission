package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserRepository userRepository;

    @Override
    public ChannelPublicCreateResponseDto createPublicChannel(PublicChannelCreateRequest publicChannelCreateRequest) {
        Channel channel = channelMapper.toPublicChannel(publicChannelCreateRequest);
        channel.setActive(ActiveStatus.ACTIVE);
        channelRepository.save(channel);
        return channelMapper.toChannelPublicCreateResponseDto(channel);
    }

    @Override
    public ChannelPrivateCreateResponseDto createPrivateChannel(PrivateChannelCreateRequest privateChannelCreateRequest) {
        for (UUID id : privateChannelCreateRequest.participantIds()) {
            validateUser(id);
        }

        Channel channel = channelMapper.toPrivateChannel(privateChannelCreateRequest);
        channel.setActive(ActiveStatus.ACTIVE);
        channelRepository.save(channel);

        for (UUID id : privateChannelCreateRequest.participantIds()) {
            ReadStatus readStatus = new ReadStatus(id, channel.getId(), Instant.now());
            readStatusRepository.save(readStatus);
        }

        return channelMapper.toChannelPrivateCreateResponseDto(channel, readStatusRepository.findByChannelId(channel.getId()));
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        validateUser(userId);
        List<ChannelDto> channelDtos = new ArrayList<>();
        channelRepository.findAll().stream()
                .filter(channel -> validateUserInChannel(channel, userId)

                        || channel.getType().equals(ChannelType.PUBLIC))
                .forEach(channel -> {
                    Instant latestTime = findLatestMessage(channel.getMessageIds());
                    List<UUID> ids = findAllUserInChannel(channel.getId());
                    channelDtos.add(channelMapper.ofChannelDto(channel, latestTime, ids));
                });
        return channelDtos;
    }

    @Override
    public ChannelResponseDto findByChannelId(UUID channelId) {
        validateActiveChannel(channelId);

        Channel channel = channelRepository.findById(channelId);
        Instant latestTime = findLatestMessage(channel.getMessageIds());
        return channelMapper.ofChannelResponseDto(channel, latestTime);
    }

    @Override
    public ChannelResponseDto updateChannel(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest) {
        validateActiveChannel(channelId);
        validatePublicChannel(channelId);

        Channel findChannel = channelRepository.findById(channelId);
        Optional.ofNullable(publicChannelUpdateRequest.newName()).ifPresent(findChannel::setChannelName);
        Optional.ofNullable(publicChannelUpdateRequest.newDescription()).ifPresent(findChannel::setDescription);
        findChannel.setUpdatedAt(Instant.now());

        channelRepository.save(findChannel);
        Instant latestMessage = findLatestMessage(findChannel.getMessageIds());
        return channelMapper.ofChannelResponseDto(findChannel, latestMessage);
    }

    @Override
    public void deleteChannel(UUID id) {
        validateActiveChannel(id);
        Channel channel = channelRepository.findById(id);
        /***********************************
         * 전체 메시지 중 해당 채널의 메시지 삭제
         ***********************************/
        channel.getMessageIds().stream()
                .forEach(messageId -> {
                    System.out.println(channel);
                    removeMessage(channel, messageId);
                });// 채널 내 모든 메세지 삭제
        channel.clearMessages();
        channel.setActive(ActiveStatus.DELETE);
        channelRepository.delete(channel);  // 전체 채널 리스트에서 해당 채널 삭제
        readStatusRepository.delete(channel.getId());
    }

    private void removeMessage(Channel channel, UUID messageId) {
        validateActiveChannel(channel.getId());
        if (!channel.getMessageIds().contains(messageId))
            throw new IllegalStateException(messageId + " Message does not exist");

        Message message = messageRepository.findById(messageId);
        message.setActive(ActiveStatus.DELETE);
        message.setUpdatedAt(Instant.now());
        messageRepository.delete(message);
    }


    // 테스트용 내용확인
    @Override
    public List<ChannelResponseDto> findAllChannels() {
        List<ChannelResponseDto> channelResponseDtos = new ArrayList<>();
        channelRepository.findAll().stream()
                .forEach(channel -> {
                    Instant latestTime = findLatestMessage(channel.getMessageIds());
                    channelResponseDtos.add(channelMapper.ofChannelResponseDto(channel, latestTime));
                });
        return channelResponseDtos;
    }

    public void addUser(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId);
        channel.addUser(userId);
        ReadStatus readStatus = new ReadStatus(channelId, userId, Instant.now());
        readStatusRepository.save(readStatus);
    }

    private void validateActiveChannel(UUID id) {
        if (!channelRepository.findById(id).getActive().equals(ActiveStatus.ACTIVE))
            throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND);
    }

    private void validatePublicChannel(UUID id) {
        if (channelRepository.findById(id).getType().equals(ChannelType.PRIVATE))
            throw new BusinessLogicException(ExceptionCode.PRIVATE_CHANNEL_CANNOT_UPDATE);
    }

    private Instant findLatestMessage(List<UUID> messageIds) {
        List<Message> messages = messageRepository.findAll().stream()
                .filter(message -> messageIds.contains(message.getId()))
                .toList();
        Optional<Message> latestMessage = messages.stream()
                .max(Comparator.comparing(Message::getUpdatedAt));
        if (latestMessage.isEmpty()) return Instant.MIN;
        return latestMessage.get().getUpdatedAt();
    }

    private boolean validateUserInChannel(Channel channel, UUID userId) {
        return readStatusRepository.findAll().stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channel.getId()));
    }

    private void validateUser(UUID userId) {
        userRepository.findById(userId);
    }

    private List<UUID> findAllUserInChannel(UUID channelId) {
        List<UUID> ids = new ArrayList<>();
        readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .forEach(readStatus -> ids.add(readStatus.getUserId()));
        return ids;
    }
}
