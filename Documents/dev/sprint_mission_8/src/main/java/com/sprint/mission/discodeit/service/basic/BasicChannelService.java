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
import com.sprint.mission.discodeit.exception.channel.ChannelNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
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
        log.info("공개 채널 생성 요청: channelname={}", dto.getName());
        //채널명 중복 확인
        if (channelRepository.existsByName(dto.getName())) {
            log.warn("공개 채널 생성 실패-이미 존재하는 채널명: channelname={}", dto.getName());
            throw new ChannelNameAlreadyExistsException(dto.getName());
        }
        Channel channel = channelMapper.publicChannelCreateDtoToChannel(dto);
        Channel savedPubChannel = channelRepository.save(channel);
        log.info("공개 채널 생성 완료: channelname={}", dto.getName());
        return channelMapper.channelToChannelDto(savedPubChannel);
    }

    @Transactional
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateDto dto) {
        log.info("비공개 채널 생성 요청: channelname={}", dto.getName());
        //채널명 중복 확인
        if (channelRepository.existsByName(dto.getName())) {
            log.warn("비공개 채널 생성 실패-이미 존재하는 채널명: channelname={}", dto.getName());
            throw new ChannelNameAlreadyExistsException(dto.getName());
        }

        //참여자 목록 저장
        List<UUID> participantId = dto.getParticipants();
        Channel channel = channelMapper.privateChannelCreateDtoToChannel(dto);
        List<User> participants = userRepository.findAllById(participantId);
        Channel savedChannel = channelRepository.save(channel);
        log.info("비공개 채널 참여자 목록 저장 완료: participants={}", dto.getParticipants());

        //참여자 상태 설정
        for (User participant : participants) {
            ReadStatus readStatus = readStatusMapper.toEntity(participant, channel, Instant.now());
            readStatusRepository.save(readStatus);
            log.info("비공개 채널 참여자 상태 설정 완료");
        }

        List<UserDto> participantDto = participants.stream()
                .map(userMapper::userToUserDto)
                .toList();
        log.info("비공개 채널 생성 완료: channelname={}", dto.getName());
        return channelMapper.channelToChannelDto(savedChannel,participantDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

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
                .distinct()
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
    }

    @Transactional
    @Override
    public ChannelDto update(ChannelUpdateDto dto) {
        log.info("채널 정보 업데이트 요청: channelId={}", dto.getChannelId());
        if (channelRepository.existsByName(dto.getNewName())) {
            log.warn("채널 정보 업데이트 실패-이미 존재하는 채널명: channelId={}, newChannelName={}", dto.getChannelId(), dto.getNewName());
            throw new ChannelNameAlreadyExistsException(dto.getNewName());
        }

        Channel channel = channelRepository.findById(dto.getChannelId())
                .orElseThrow(() -> {
                    log.error("채널 정보 업데이트 실패-존재하지 않는 ID: channelId={}", dto.getChannelId());
                    return new ChannelNotFoundException(dto.getChannelId());
                });

        if (channel.getType() == ChannelType.PRIVATE) {
            log.error("채널 정보 업데이트 실패-비공개 채널 정보 수정 불가: channelId={}", dto.getChannelId());
            throw new PrivateChannelUpdateException(dto.getChannelId());
        }
        channel.update(dto.getNewName(), dto.getNewDescription());
        log.info("채널 정보 업데이트 완료: channelId={}", dto.getChannelId());
        return channelMapper.channelToChannelDto(channel,null);
    }

    @Override
    public void delete(UUID channelId) {
        log.info("채널 정보 삭제 요청: channelId={}", channelId);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.error("채널 정보 삭제 실패-존재하지 않는 ID: channelId={}", channelId);
                    return new ChannelNotFoundException(channelId);
                });
        readStatusRepository.deleteByChannelId(channelId);
        messageRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
        log.info("<채널 정보 삭제 완료: channelId={}", channelId);
    }
}