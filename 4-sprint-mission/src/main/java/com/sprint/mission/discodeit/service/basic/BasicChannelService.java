package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final ReadStatusMapper readStatusMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public ChannelResponseDto createPublicChannel(ChannelCreateDto channelCreateDto) {
        Channel channel = channelMapper.channelCreateDtoToChannel(channelCreateDto);

        UUID creatorId = channelCreateDto.creatorId();
        User creator =
                userRepository
                        .findById(creatorId)
                        .orElseThrow(() -> new IllegalArgumentException("생성자 유저가 존재하지 않습니다."));
        channel.addUser(creator);

        channelRepository.save(channel);

        List<UUID> participantIds = channel.getUserIds();

        return channelMapper.channelToChannelResponseDto(channel, null, participantIds);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(List<UserResponseDto> users) {
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);

        for (UserResponseDto userDto : users) {
            User user =
                    userRepository
                            .findById(userDto.id())
                            .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
            channel.addUser(user);
        }

        channelRepository.save(channel);

        for (UserResponseDto userDto : users) {
            ReadStatusRequestDto dto = new ReadStatusRequestDto(userDto.id(), channel.getId());
            ReadStatus readStatus = readStatusMapper.readStatusReqestDtoToReadStatus(dto);
            readStatusRepository.save(readStatus);
        }

        return channelMapper.channelToChannelResponseDto(channel, null, channel.getUserIds());
    }

    private boolean isAccessibleChannel(Channel channel, UUID userId) {
        return channel.getType() == ChannelType.PUBLIC || channel.getUserIds().contains(userId);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(
                        channel ->
                                channel.getType() == ChannelType.PUBLIC
                                        || channel.getUserIds().contains(userId)) // 수정된 조건
                .map(
                        channel -> {
                            Instant lastMessageTime = getLastMessageTime(channel);
                            List<UUID> participantIds = getParticipantIds(channel);
                            return channelMapper.channelToChannelResponseDto(
                                    channel, lastMessageTime, participantIds);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponseDto findChannelById(UUID channelId) {
        Channel channel =
                channelRepository
                        .findById(channelId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        Instant lastMessageTime = getLastMessageTime(channel);
        List<UUID> participantIds = getParticipantIds(channel);

        return channelMapper.channelToChannelResponseDto(channel, lastMessageTime, participantIds);
    }

    @Override
    public List<ChannelResponseDto> findChannelsByNameContains(String channelName) {
        return channelRepository.findAll().stream()
                .filter(
                        channel -> channel.getChannelName().toLowerCase().contains(channelName.toLowerCase()))
                .map(
                        channel -> {
                            Instant lastMessageTime = getLastMessageTime(channel);

                            List<UUID> participantIds = getParticipantIds(channel);

                            return channelMapper.channelToChannelResponseDto(
                                    channel, lastMessageTime, participantIds);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseDto> findMessagesByUserInChannel(UUID channelId, UUID userId) {
        Channel findChannel = isExistedChannel(channelId);
        isExistedUser(userId);

        return findChannel.getMessageIds().stream()
                .map(messageRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(message -> message.getState() != MessageState.DELETED)
                .filter(message -> message.getAuthorId().equals(userId))
                .map(messageMapper::messageToMessageResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelResponseDto updateChannelName(ChannelUpdateDto channelUpdateDto) {
        Channel channel = isExistedChannel(channelUpdateDto.id());

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널은 이름을 수정할 수 없습니다.");
        }

        Optional.ofNullable(channelUpdateDto.name()).ifPresent(channel::setChannelName);

        channelRepository.save(channel);

        Instant lastMessageTime = getLastMessageTime(channel);
        List<UUID> participantIds = getParticipantIds(channel);

        return channelMapper.channelToChannelResponseDto(channel, lastMessageTime, participantIds);
    }

    @Override
    public void joinUser(UUID channelId, UUID userId) {
        Channel findChannel = isExistedChannel(channelId);
        User findUser = isExistedUser(userId);
        findChannel.addUser(findUser);
        channelRepository.save(findChannel);
    }

    @Override
    public void leaveUser(UUID channelId, UUID userId) {
        Channel findChannel = isExistedChannel(channelId);
        User findUser = isExistedUser(userId);
        findChannel.removeUser(findUser);
        channelRepository.save(findChannel);
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel =
                channelRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        for (UUID messageId : channel.getMessageIds()) {
            messageRepository
                    .findById(messageId)
                    .ifPresent(
                            message -> {
                                for (UUID attachmentId : message.getAttachmentIds()) {
                                    binaryContentRepository.delete(attachmentId);
                                }
                                messageRepository.delete(message.getId());
                            });
        }

        readStatusRepository.delete(id);
        channelRepository.delete(id);
    }

    private Channel isExistedChannel(UUID channelId) {
        return channelRepository
                .findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
    }

    private User isExistedUser(UUID userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }

    private Instant getLastMessageTime(Channel channel) {
        return channel.getMessageIds().stream()
                .map(messageRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private List<UUID> getParticipantIds(Channel channel) {
        return channel.getUserIds().stream()
                .map(userRepository::findById)
                .flatMap(Optional::stream)
                .map(User::getId)
                .toList();
    }
}
