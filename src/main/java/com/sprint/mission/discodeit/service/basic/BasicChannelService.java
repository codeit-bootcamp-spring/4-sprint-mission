package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;

    //
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;
    private final MessageRepository messageRepository;

    @Override
    public ChannelResponseDto create(ChannelPublicCreateDto channelPublicCreateDto) {
        // DTO 유효성 검사
        if (channelPublicCreateDto == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create 실패: channelPublicCreateDto is empty");
        }

        // PUBLIC 타입 채널 생성용 메서드이므로, PRIVATE은 PRIVATE 전용을 이용해야함.
        if (channelPublicCreateDto.channelType() != ChannelType.PUBLIC) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create 실패: PRIVATE 타입 채널은 전용 메서드를 이용해주십시오.");
        }
        
        // 채널명 중복 검사
        if (detectChannelNameIsDuplicated(channelPublicCreateDto.channelName())) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create 실패: 채널 이름은 중복될 수 없습니다.");
        }

        Channel channelPublic = channelMapper.channelPublicCreateDtoToChannel(channelPublicCreateDto);
        channelRepository.save(channelPublic);
        return channelMapper.channelToChannelResponseDto(channelPublic, null, null);
    }

    @Override
    public ChannelResponseDto create(ChannelPrivateCreateDto channelPrivateCreateDto) {
        // DTO 유효성 검사
        if (channelPrivateCreateDto == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create 실패: channelPrivateCreateDto is empty");
        }

        // PRIVATE 타입 채널 생성용 메서드이므로, PUBLIC은 PUBLIC 전용을 이용해야함.
        if (channelPrivateCreateDto.channelType() != ChannelType.PRIVATE) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".create 실패: PUBLIC 타입 채널은 전용 메서드를 이용해주십시오.");
        }

        Channel channelPrivate = channelMapper.channelPrivateCreateDtoToChannel(channelPrivateCreateDto);
        saveReadStatusAsMuchAsUserId(channelPrivateCreateDto.readStatusCreateDtos(), channelPrivate.getId());
        channelRepository.save(channelPrivate);
        return channelMapper.channelToChannelResponseDto(channelPrivate, null, channelPrivateCreateDto.userIds());
    }

    @Override
    public ChannelResponseDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));

        Instant latestMessageTime = getLastestMessageTime(channel);

        // 채널 타입이 PRIVATE인 경우 유저 아이디 목록을 포함하여 반환함
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());
            List<UUID> participantUserIds = getUserIdsFromReadStatuses(readStatuses);
            return channelMapper.channelToChannelResponseDto(channel, latestMessageTime, participantUserIds);
        }

        // 채널 타입이 PRIVATE이 아닌 경우(PUBLIC)인 경우 유저 아이디 목록은 null
        return channelMapper.channelToChannelResponseDto(channel,latestMessageTime, null);
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<ChannelResponseDto> channelResponseDtos = new ArrayList<>();

        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            Instant latestMessageTime = getLastestMessageTime(channel);
            
            // PRIVATE 채널은 참여한 회원만 조회할 수 있음
            if (channel.getChannelType() == ChannelType.PRIVATE) {
                List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channel.getId());
                ReadStatus privateReadStatus  = readStatuses.stream()
                        .filter(readStatus -> readStatus.getUserId().equals(userId))
                        .findFirst()
                        .orElse(null);

                if (privateReadStatus == null) {
                    System.out.println(this.getClass().getSimpleName() + ".findAllByUserId() 실패: Private read status is null; 참여하지 않은 PRIVATE 채널은 조회할 수 없습니다.");
                    continue;
                }

                List<UUID> participantUserIds = getUserIdsFromReadStatuses(readStatuses);
                channelResponseDtos.add(channelMapper.channelToChannelResponseDto(channel, latestMessageTime, participantUserIds));
            }
            // PUBLIC 채널은 참여 회원 목록을 표시하지 않음
            channelResponseDtos.add(channelMapper.channelToChannelResponseDto(channel, latestMessageTime, null));
        }
        return channelResponseDtos;
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateDto channelUpdateDto) {
        Channel updatedChannel = channelRepository.findById(channelUpdateDto.id())
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelUpdateDto.id() + " not found"));

        // PRIVATE 채널은 수정할 수 없음
        if (updatedChannel.getChannelType() == ChannelType.PRIVATE) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "update() 실패: Private channel cannot be updated");
        }
        
        // 채널명 중복 검사
        if (detectChannelNameIsDuplicated(channelUpdateDto.newChannelName())) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ".update 실패: 채널 이름은 중복될 수 없습니다.");
        }

        updatedChannel.update(channelUpdateDto.newChannelName(), channelUpdateDto.newDescription());
        channelRepository.save(updatedChannel);
        return channelMapper.channelToChannelResponseDto(updatedChannel, getLastestMessageTime(updatedChannel), null);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        
        // 메시지 삭제
        List<Message> channelMessages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
        
        for (Message message : channelMessages) {
            messageRepository.deleteById(message.getId());
        }
        
        // ReadStatus 삭제
        List<ReadStatus> channelReadStatuses = readStatusRepository.findAllByChannelId(channelId);
        for (ReadStatus readStatus : channelReadStatuses) {
            readStatusRepository.delete(readStatus.getId());
        }
        
        // 채널 삭제
        channelRepository.deleteById(channelId);
    }

    private void saveReadStatusAsMuchAsUserId(List<ReadStatusCreateDto> readStatusCreateDtos, UUID channelId) {
        for (ReadStatusCreateDto readStatusCreateDto : readStatusCreateDtos) {
            ReadStatus createdReadStatus = readStatusMapper.readStatusCreateDtoToReadStatus(readStatusCreateDto);
            createdReadStatus.setChannelId(channelId);
            readStatusRepository.save(createdReadStatus);
        }
    }

    private Instant getLastestMessageTime(Channel channel) {
        List<Message> channelMessages = messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channel.getId()))
                .toList();

        if (channelMessages.isEmpty()) return null;

        return findLastestMessageTime(channelMessages);
    }

    private Instant findLastestMessageTime(List<Message> channelMessages) {
        return channelMessages.stream()
                .map(Message::getUpdatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    private List<UUID> getUserIdsFromReadStatuses(List<ReadStatus> readStatuses) {
        return readStatuses.stream()
                .map(ReadStatus::getUserId)
                .toList();
    }

    private boolean detectChannelNameIsDuplicated(String channelName) {
        Optional<Channel> duplicatedChannel = channelRepository.findAll().stream()
                .filter(channel -> channel.getChannelName().equals(channelName))
                .findFirst();
        return duplicatedChannel.isPresent();
    }
}
