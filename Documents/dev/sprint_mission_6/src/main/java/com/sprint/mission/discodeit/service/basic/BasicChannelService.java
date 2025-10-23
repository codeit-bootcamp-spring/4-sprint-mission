package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReadStatusMapper readStatusMapper;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateDto dto) {
        //채널명 중복 확인
        if (channelRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }
        Channel channel = channelMapper.publicChannelCreateDtoToChannel(dto);
        Channel savedPubChannel = channelRepository.save(channel);
        return channelMapper.channelToChannelDto(savedPubChannel);
    }

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateDto dto) {
        //채널명 중복 확인
        if (channelRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }

        //참여자 목록 저장
        List<UUID> participantId = dto.getParticipants();
        Channel channel = channelMapper.privateChannelCreateDtoToChannel(dto);
        List<User> participants = userRepository.findAllById(participantId);
        Channel savedChannel = channelRepository.save(channel);

        //참여자 상태 설정
        for (User participant : participants) {
            ReadStatus readStatus = readStatusMapper.toEntity(participant, channel, Instant.now());
            readStatusRepository.save(readStatus);
        }

        List<UserDto> participantDto = participants.stream()
                .map(userMapper::userToUserDto)
                .toList();
        return channelMapper.channelToChannelDto(savedChannel,participantDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("채널 아이디" + channelId + "에 해당하는 채널을 찾지 못했습니다."));

        List<UserDto> participants = null;
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                    .map(readStatus -> readStatus.getUser())
                    .map(userMapper::userToUserDto)
                    .toList();
        }
        return channelMapper.channelToChannelDto(channel, participants);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

// 1. private 채널 ID 목록 추출
        List<UUID> privateChannelIds = readStatuses.stream()
                .map(rs -> rs.getChannel().getId())
                .toList();

// 2. private 채널 가져오기
        List<Channel> privateChannels = channelRepository.findAllById(privateChannelIds);

// 3. public 채널 가져오기
        List<Channel> publicChannels = channelRepository.findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
                .toList();

// 4. 채널 합치기
        List<Channel> allTypeChannels = new ArrayList<>(privateChannels);
        allTypeChannels.addAll(publicChannels);

// 5. DTO 변환 (이미 조회한 readStatuses를 다시 활용)
        return allTypeChannels.stream()
                .map(channel -> {
                    List<UUID> memberIds = new ArrayList<>();
                    if (channel.getType() == ChannelType.PRIVATE) {
                        memberIds = readStatuses.stream()
                                .filter(rs -> rs.getChannel().getId().equals(channel.getId()))
                                .map(rs -> rs.getUser().getId())
                                .toList();
                        List<UserDto> memberDto = memberIds.stream()
                                .map(memberId -> userMapper.userToUserDto(userRepository.findById(userId).orElseThrow()))
                                .toList();
                        return channelMapper.channelToChannelDto(channel, memberDto);
                    }
                    return channelMapper.channelToChannelDto(channel, null);
                })
                .toList();
//        //Private 채널: 사용자가 속한 채널의 ID 조회
//        List<UUID> privateChannelIds = readStatusRepository.findAllByUserId(userId)
//                .stream()
//                .map(readStatus -> readStatus.getChannel().getId())
//                .toList();
//        //Private 채널 엔티티 조회
//        List<Channel> privateChannels = channelRepository.findAllById(privateChannelIds);
//
//        //Public 채널 전체 조회
//        List<Channel> publicChannels = channelRepository.findAll()
//                .stream()
//                .filter(channel -> channel.getType() == ChannelType.PUBLIC)
//                .toList();
//        if (privateChannels.isEmpty() && publicChannels.isEmpty()) {
//            return List.of();
//        }
//
//        //Private + Public 채널 통합
//        List<Channel> allTypeChannels = new ArrayList<>(privateChannels);
//        allTypeChannels.addAll(publicChannels);
//        //DTO로 변환(Private 채널에만 memberIds 포함)
//        return allTypeChannels.stream()
//                .map(channel -> {
//                    List<UUID> memberIds = null;
//                    if (channel.getType() == ChannelType.PRIVATE) {
//                        memberIds = readStatusRepository.findAllByChannelId(channel.getId())
//                                .stream()
//                                .map(readStatus -> readStatus.getUser().getId())
//                                .toList();
//                    }
//                    return channelMapper.channelToChannelDto(channel, memberIds);
//                })
//                .toList();
    }

    @Transactional
    @Override
    public ChannelDto update(ChannelUpdateDto dto) {
        if (channelRepository.existsByName(dto.getNewName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }

        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("채널 아이디 " + dto.getChannelId() + " 에 해당하는 채널을 찾지 못했습니다."));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new NoSuchElementException("PRIVATE 채널은 수정할 수 없습니다.");
        }
        channel.update(dto.getNewName(), dto.getNewDescription());
        return channelMapper.channelToChannelDto(channel,null);
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