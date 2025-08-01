package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest req) {
        if (req.name() == null) {
            throw new IllegalArgumentException("채널 생성에 필요한 필드가 누락되었습니다.");
        }
        Channel channel = channelMapper.publicChannelCreateDtoToChannel(req);
        channelRepository.save(channel);

        List<UserDto> participants = new ArrayList<>();
        return channelMapper.channelToChannelDto(channel, null, participants);
    }

    @Override
    public ChannelDto createPrivateChannel(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("채널에 참여할 유저 목록이 필요합니다.");
        }

        Channel channel = new Channel("Private Channel", "", ChannelType.PRIVATE);
        channelRepository.save(channel);

        List<UserDto> participants = new ArrayList<>();
        for (UUID userId : userIds) {
            User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다: " + userId));

            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);

            BinaryContentDto binaryContentDto = toBinaryContentDto(user.getProfile());
            participants.add(userMapper.userToUserDto(user, binaryContentDto));
        }

        return channelMapper.channelToChannelDto(channel, null, participants);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

        List<Channel> privateChannels = readStatusRepository.findAllByUserId(userId).stream()
            .map(ReadStatus::getChannel)
            .filter(channel ->  channel.getType().equals(ChannelType.PRIVATE))
            .toList();

        Set<Channel> allChannels = new HashSet<>();
        allChannels.addAll(publicChannels);
        allChannels.addAll(privateChannels);

        return allChannels.stream()
                .map(
                        channel -> {
                            Instant lastMessageTime = getLastMessageTime(channel);
                            List<UserDto> participants = getParticipants(channel);
                            return channelMapper.channelToChannelDto(channel, lastMessageTime, participants);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public ChannelDto findChannelById(UUID channelId) {
        Channel channel =
                channelRepository
                        .findById(channelId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        Instant lastMessageTime = getLastMessageTime(channel);
        List<UserDto> participants = getParticipants(channel);

        return channelMapper.channelToChannelDto(channel, lastMessageTime, participants);
    }

    @Override
    public List<ChannelDto> findChannelsByNameContains(String channelName) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getName().toLowerCase().contains(channelName.toLowerCase()))
                .map(
                        channel -> {
                            Instant lastMessageTime = getLastMessageTime(channel);
                            List<UserDto> participants = getParticipants(channel);
                            return channelMapper.channelToChannelDto(channel, lastMessageTime, participants);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> findMessagesByUserInChannel(UUID channelId, UUID userId) {
        boolean exists = readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
        if (!exists) {
            throw new IllegalArgumentException("해당 채널에 참여하지 않은 사용자입니다.");
        }

        return messageRepository.findAllByChannelIdAndAuthorId(channelId, userId).stream()
                .map(message -> messageMapper.messageToMessageDto(message,messageToBinaryContentDto(message)))
                .collect(Collectors.toList());
    }

    @Override
    public ChannelDto updateChannelName(PublicChannelUpdateRequest req) {
        Channel channel = isExistedChannel(req.id());

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalStateException("비공개 채널은 이름을 수정할 수 없습니다.");
        }

        Optional.ofNullable(req.name()).ifPresent(channel::setName);
        Optional.ofNullable(req.description()).ifPresent(channel::setDescription);

        channelRepository.save(channel);

        Instant lastMessageTime = getLastMessageTime(channel);
        List<UserDto> participants = getParticipants(channel);

        return channelMapper.channelToChannelDto(channel, lastMessageTime, participants);
    }

    @Override
    public void joinUser(UUID channelId, UUID userId) {
        User user = isExistedUser(userId);
        Channel channel = isExistedChannel(channelId);

        boolean exists = readStatusRepository.existsByUserIdAndChannelId(userId, channelId);

        if (exists) {
            throw new IllegalStateException("이미 채널에 참여 중입니다.");
        }

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.save(readStatus);
    }

    @Override
    public void leaveUser(UUID channelId, UUID userId) {
        User user = isExistedUser(userId);
        Channel channel = isExistedChannel(channelId);

        boolean exists = readStatusRepository.existsByUserIdAndChannelId(userId, channelId);

        if (!exists) {
            throw new IllegalStateException("채널에 참여하고 있지 않습니다.");
        }

        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatusRepository.delete(readStatus);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel =
                channelRepository
                        .findById(channelId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));

        List<Message> messages = messageRepository.findAllByChannelId(channelId);

        List<BinaryContent> allBinaryContents =
                messages.stream()
                        .flatMap(message -> message.getAttachments().stream())
                        .collect(Collectors.toList());
        binaryContentRepository.deleteAll(allBinaryContents);

        messageRepository.deleteAll(messages);
        List<ReadStatus> readStatus = readStatusRepository.findAllByChannelId(channelId);
        readStatusRepository.deleteAll(readStatus);
        channelRepository.delete(channel);
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
        return messageRepository
                .findFirstByChannelIdOrderByCreatedAtDesc(channel.getId())
                .map(Message::getCreatedAt)
                .orElse(null);
    }

    private List<UserDto> getParticipants(Channel channel) {
        return readStatusRepository.findAllByChannelId(channel.getId()).stream()
            .map(ReadStatus::getUser)
            .map(user -> {
                BinaryContentDto binaryContentDto = toBinaryContentDto(user.getProfile());
                return userMapper.userToUserDto(user, binaryContentDto);
            })
            .collect(Collectors.toList());
    }

    private BinaryContentDto toBinaryContentDto(BinaryContent binaryContent) {
        if (binaryContent == null) return null;

        try (InputStream inputStream = binaryContentStorage.get(binaryContent.getId())) {
            byte[] bytes = inputStream.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return binaryContentMapper.binaryContentToBinaryContentDto(binaryContent, base64);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 로딩 실패", e);
        }
    }

    private List<BinaryContentDto> messageToBinaryContentDto(Message message) {
        return message.getMessageAttachments().stream()
            .map(MessageAttachment::getAttachment)
            .map(attachment -> {
                try (InputStream in = binaryContentStorage.get(attachment.getId())) {
                    byte[] bytes = in.readAllBytes();
                    String encoded = Base64.getEncoder().encodeToString(bytes);
                    return binaryContentMapper.binaryContentToBinaryContentDto(attachment, encoded);
                } catch (IOException e) {
                    throw new RuntimeException("BinaryContent 로드 실패: " + attachment.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }
}
