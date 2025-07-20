package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto createPublicChannel(PublicChannelCreateDto dto) {
        if (channelRepository.existsByName(dto.getChannelName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }
        Channel channel = new Channel(dto.getChannelName(), dto.getDescription(), ChannelType.PUBLIC);
        Channel savedPubChannel = channelRepository.save(channel);
        return channelMapper.channelToChannelResponseDto(savedPubChannel);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto) {
        if (channelRepository.existsByName(dto.getChannelName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }

        List<UUID> membersId = dto.getMembers();
        Channel channel = new Channel(membersId, ChannelType.PRIVATE);

        List<User> members = userRepository.findAllById(membersId);
        for (User member : members) {
            ReadStatus readStatus = new ReadStatus(channel.getId(), member.getId(), Instant.now());
            readStatusRepository.save(readStatus);
            channel.getMembers().add(member.getId());
        }
        Channel savedChannel = channelRepository.save(channel);
        return channelMapper.channelToChannelResponseDto(savedChannel);
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널 아이디" + channelId + "에 해당하는 채널을 찾지 못했습니다."));

        List<UUID> memberIds = new ArrayList<>();
        if (channel.getType() == ChannelType.PRIVATE) {
            memberIds = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(ReadStatus::getUserId)
                    .toList();
        }
        return channelMapper.channelToChannelResponseDto(channel, memberIds);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        //Private 채널: 사용자가 속한 채널의 ID 조회
        List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(ReadStatus::getChannelId)
                .toList();
        //Private 채널 엔티티 조회
        List<Channel> privateChannels = channelRepository.findAllById(privateChannelIds);
        //Public 채널 전체 조회
        List<Channel> publicChannels = channelRepository.findAll()
                .stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .toList();
        if (privateChannels.isEmpty() && publicChannels.isEmpty()) {
            return List.of();
        }
        //Private + Public 채널 통합
        List<Channel> allTypeChannels = new ArrayList<>();
        allTypeChannels.addAll(privateChannels);
        allTypeChannels.addAll(publicChannels);
        //DTO로 변환(Private 채널에만 memberIds 포함)
        return allTypeChannels.stream()
                .map(channel -> {
                    List<UUID> memberIds = null;
                    if (channel.getType() == ChannelType.PRIVATE) {
                        memberIds = readStatusRepository.findAllByChannelId(channel.getId())
                                .stream()
                                .map(ReadStatus::getUserId)
                                .toList();
                    }
                    return channelMapper.channelToChannelResponseDto(channel, memberIds);
                })
                .toList();
    }


    @Override
    public ChannelResponseDto update(ChannelUpdateDto dto) {
        if (channelRepository.existsByName(dto.getNewName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }

        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("채널 아이디 " + dto.getChannelId() + " 에 해당하는 채널을 찾지 못했습니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new NoSuchElementException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.update(dto.getNewName(), dto.getNewDescription());
        Channel updatedChannel = channelRepository.save(channel);
        return channelMapper.channelToChannelResponseDto(updatedChannel, null);
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널 아이디" + channelId + " 에 해당하는 유저를 찾지 못했습니다."));
        readStatusRepository.deleteById(channelId);
        messageRepository.deleteById(channelId);
        channelRepository.deleteById(channelId);
    }
}