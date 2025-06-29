package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelMapper.channelToResponseDto(channelRepository.save(channel), null, null);
    }

    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelCreateDto publicChannelCreateDto) {
        isExistChannelByChannelName(publicChannelCreateDto.getName());

        Channel channel = channelMapper.publicDtoToChannel(publicChannelCreateDto);
        channelRepository.save(channel);

        return channelMapper.channelToResponseDto(channel, null, null);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto privateChannelCreateDto) {
        Channel channel = channelMapper.privateDtoToChannel();


        List<User> users = userRepository.findAllById(privateChannelCreateDto.getUserIds());

        for (User user : users) {
            ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
            readStatusRepository.save(readStatus);
        }

        channelRepository.save(channel);
        return channelMapper.channelToResponseDto(channel, null, privateChannelCreateDto.getUserIds());
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Optional<Channel> channel = channelRepository.findById(channelId);

        if (channel.isEmpty()) {
            throw new NoSuchElementException("ID가 " + channelId + "인 채널을 찾을 수 없습니다.");
        }

        List<Message> messages = getMessgesFromChannelId(channelId);

        List<UUID> userIds = getUserIdFromChannel(channel.get());

        return channelMapper.channelToResponseDto(channel.get(), messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreatedAt(), userIds);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<UUID> channelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        List<Channel> Channels = channelRepository.findAllByChannelIds(channelIds);
        return Channels.stream()
                .map(channel -> {
                    List<Message> messages = getMessgesFromChannelId(channel.getId());

                    List<UUID> userIds = getUserIdFromChannel(channel);

                    return channelMapper.channelToResponseDto(channel, messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreatedAt(), userIds);
                })
                .toList();
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateRequestDto channelUpdateRequestDto) {
        Channel channel = channelRepository.findById(channelUpdateRequestDto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("ID가 " + channelUpdateRequestDto.getChannelId() + "인 채널을 찾을 수 없습니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        isExistChannelByChannelName(channelUpdateRequestDto.getName());

        channel.update(channelUpdateRequestDto);
        channelRepository.save(channel);

        List<Message> messages = getMessgesFromChannelId(channel.getId());

        List<UUID> userIds = getUserIdFromChannel(channel);

        return channelMapper.channelToResponseDto(channel, messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreatedAt(), userIds);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("ID가 " + channelId + "인 채널을 찾을 수 없습니다.");
        }

        messageRepository.deleteAllByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    private List<Message> getMessgesFromChannelId (UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    private List<UUID> getUserIdFromChannel(Channel channel){
       return channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUserId)
                .toList()
                : null;
    }

    private void isExistChannelByChannelName(String channelName) {
        Optional<Channel> channel = channelRepository.findByChannelName(channelName);
        if(channel.isPresent()) {
            throw new IllegalArgumentException("이미 생생된 채널 이름입니다.");
        }
    }
}
