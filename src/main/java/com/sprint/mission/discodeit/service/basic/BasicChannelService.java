package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto createPublicChannel(ChannelCreateDto dto) {
        dto.setChannelType(ChannelType.PUBLIC);
        validateCreateChannel(dto, ChannelType.PUBLIC);
        Channel channel = channelMapper.toEntity(dto);

        channelRepository.save(channel);
        return channelMapper.toDto(channel, List.of());
    }

    @Override
    public ChannelResponseDto createPrivateChannel(ChannelCreateDto dto) {
        dto.setChannelType(ChannelType.PRIVATE);
        validateCreateChannel(dto, ChannelType.PRIVATE);
        Channel channel = channelMapper.toEntity(dto);
        Channel saved = channelRepository.save(channel);

        // PRIVATE 채널에 참여하고 있는 유저의 ReadStatus 생성
        for (UUID participantId : dto.getParticipantIds()) {
            readStatusRepository.save(new ReadStatus(participantId, saved.getId(), Instant.now()));
        }
        return channelMapper.toDto(saved, dto.getParticipantIds());
    }

    @Override
    public ChannelResponseDto find(UUID channelId, UUID userId) {
        Channel channel = requireChannel(channelId);
        if (channel.getType() == ChannelType.PUBLIC) {
            return channelMapper.toDto(channel, List.of());
        } else {
            List<UUID> participantIds = findParticipantIdsByChannel(channel);
            return channelMapper.toDto(channel, participantIds);
        }
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        // PUBLIC 채널
        List<ChannelResponseDto> publicDtos = channelRepository
                .findAllByChannelType(ChannelType.PUBLIC)
                .stream()
                .map(channel -> channelMapper.toDto(channel, List.of()))
                .toList();

        // PRIVATE 채널 중, 이 userId가 속한 채널만 조회
        List<ChannelResponseDto> privateDtos = readStatusRepository
                .findAllByUserId(userId)
                .stream()
                .map(this::mapPrivateChannel)
                .filter(dto -> dto.getType() == ChannelType.PRIVATE)
                .toList();

        // 두 채널 합쳐서 반환
        return Stream
                .concat(publicDtos.stream(), privateDtos.stream())
                .toList();
    }

    @Override
    public ChannelResponseDto update(UUID channelId, ChannelUpdateDto dto) {
        Channel channel = requirePublicChannel(channelId);
        // name 이 변경된 경우만
        String newName = dto.getName();
        if (newName != null && !newName.equalsIgnoreCase(channel.getName())) {
            validateUniqueName(dto.getName());
            channel.updateName(dto.getName());
        }
        // description 이 들어왔으면
        if (dto.getDescription() != null) {
            channel.updateDescription(dto.getDescription());
        }
        channelMapper.updateEntity(dto, channel);
        channelRepository.save(channel);
        return channelMapper.toDto(channel, List.of());
    }

    @Override
    public void delete(UUID channelId) {
        requireChannel(channelId);
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

    /* =========================================================
     * INTERNAL VALIDATION HELPERS
     * ========================================================= */

    /**
     * 채널 생성 시 채널 타입 및 필수 필드를 검증합니다.
     *
     * @param dto  채널 생성 DTO
     * @param type 채널 타입 (PUBLIC, PRIVATE)
     * @throws IllegalArgumentException 필수 정보가 없거나 타입이 일치하지 않는 경우
     */
    private void validateCreateChannel(ChannelCreateDto dto, ChannelType type) {
        switch(type) {
            case PUBLIC:
                if (dto.getName() == null || dto.getName().isBlank()) {
                    throw new IllegalArgumentException("Public 채널 생성 시 name 이 필요합니다.");
                }
                validateUniqueName(dto.getName());
                break;
            case PRIVATE:
                if (dto.getParticipantIds() == null || dto.getParticipantIds().isEmpty()) {
                    throw new IllegalArgumentException("Private 채널 생성 시 participantIds 가 필요합니다.");
                }
                break;
            default:
                break;
        }
    }

    /**
     * PUBLIC 채널의 이름이 이미 존재하는지 검증합니다.
     *
     * @param name 생성하려는 채널 이름
     * @throws IllegalArgumentException 중복일 경우
     */
    private void validateUniqueName(String name) {
        boolean exists = channelRepository
                .findAllByChannelType(ChannelType.PUBLIC)
                .stream()
                .anyMatch(ch -> ch.getName().equalsIgnoreCase(name.trim()));
        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 채널 이름입니다: " + name);
        }
    }

    /**
     * 주어진 채널 ID에 해당하는 채널을 조회합니다.
     *
     * @param channelId 채널 ID
     * @return 채널 엔티티
     * @throws NoSuchElementException 채널이 존재하지 않는 경우
     */
    private Channel requireChannel(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    /**
     * Public 채널만 허용하는 검증 메서드입니다.
     *
     * @param channelId 채널 ID
     * @return Public 채널
     * @throws IllegalArgumentException Public 채널이 아닌 경우
     */
    private Channel requirePublicChannel(UUID channelId) {
        Channel channel = requireChannel(channelId);
        if (channel.getType() != ChannelType.PUBLIC) {
            throw new IllegalArgumentException("Channel with id " + channelId + " is not a PUBLIC channel.");
        }
        return channel;
    }

    /**
     * 주어진 Private 의 모든 참여자 ID 리스트를 조회합니다.
     * 존재하지 않을 경우 예외를 던집니다.
     *
     * @param channel 채널 엔티티
     * @return 해당 Private 채널에 속한 모든 사용자 ID 리스트
     */
    private List<UUID> findParticipantIdsByChannel(Channel channel) {
       return readStatusRepository
                .findAllByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUserId)
                .toList();
    }

    /**
     * ReadStatus 정보를 기반으로 Private 채널의 응답 DTO를 생성합니다.
     *
     * @param rs     ReadStatus 엔티티
     * @return 채널 및 참가자 정보를 포함한 채널 응답 Dto
     */
    private ChannelResponseDto mapPrivateChannel(ReadStatus rs) {
        Channel ch = requireChannel(rs.getChannelId());
        List<UUID> participantIds = findParticipantIdsByChannel(ch);
        return channelMapper.toDto(ch, participantIds);
    }
}
