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
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelResponseDto createPublicChannel(ChannelCreateDto dto) {
        validateCreateChannel(dto, ChannelType.PUBLIC);

        Channel channel = channelMapper.toEntity(dto);
        channelRepository.save(channel);
        return channelMapper.toDto(channel, null, null);
    }

    @Override
    public ChannelResponseDto createPrivateChannel(ChannelCreateDto dto) {
        validateCreateChannel(dto, ChannelType.PRIVATE);

        Channel channel = channelMapper.toEntity(dto);
        Channel saved = channelRepository.save(channel);

        // ReadStatus 양 쪽 유저
        readStatusRepository.save(new ReadStatus(dto.getUserId(), saved.getId()));
        readStatusRepository.save(new ReadStatus(dto.getOtherUserId(), saved.getId()));

        return channelMapper.toDto(saved, dto.getUserId(), dto.getOtherUserId());
    }

    @Override
    public ChannelResponseDto find(UUID channelId, UUID userId) {
        Channel channel = requireChannel(channelId);
        if (channel.getType() == ChannelType.PUBLIC) {
            return channelMapper.toDto(channel, null, null);
        } else {
            // PRIVATE 채널의 경우 상대방 ID 조회
            UUID otherId = findOtherUser(channel, userId);
            return channelMapper.toDto(channel, otherId, userId);
        }
    }

    @Override
    public AllChannelByUserIdResponseDto findAllByUserId(UUID userId) {
        // PUBLIC 채널
        List<ChannelResponseDto> publicDtos = channelRepository
                .findAllByChannelType(ChannelType.PUBLIC)
                .stream()
                .map(channel -> channelMapper.toDto(channel, null, null))
                .toList();

        // PRIVATE 채널 중, 이 userId가 속한 채널만 조회
        List<ChannelResponseDto> privateDtos = readStatusRepository
                .findAllByUserId(userId)
                .stream()
                .map(rs -> mapPrivateChannel(rs, userId))
                .toList();

        return new AllChannelByUserIdResponseDto(publicDtos, privateDtos);
    }

    @Override
    public ChannelResponseDto update(ChannelUpdateDto channelUpdateDto) {
        Channel channel = requirePublicChannel(channelUpdateDto.getId());
        String newName = channelUpdateDto.getName();
        if (newName != null && !newName.equalsIgnoreCase(channel.getName())) {
            validateUniqueName(newName);
        }
        channelMapper.updateEntity(channelUpdateDto, channel);
        channelRepository.save(channel);
        return channelMapper.toDto(channel, null, null);
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
                if (dto.getType() != ChannelType.PUBLIC) {
                    throw new IllegalArgumentException("채널 타입은 PUBLIC 이어야 합니다.");
                }
                if (dto.getName() == null || dto.getName().isBlank()) {
                    throw new IllegalArgumentException("Public 채널 생성 시 name 이 필요합니다.");
                }
                validateUniqueName(dto.getName());
                break;
            case PRIVATE:
                if (dto.getType() != ChannelType.PRIVATE) {
                    throw new IllegalArgumentException("채널 타입은 PRIVATE 이어야 합니다.");
                }
                if (dto.getUserId() == null || dto.getOtherUserId() == null) {
                    throw new IllegalArgumentException("Private 채널 생성 시 userId 와 otherUserId 가 필요합니다.");
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
     * Private 채널에서 현재 사용자를 제외한 상대방 사용자 ID를 찾습니다.
     * 존재하지 않을 경우 예외를 던집니다.
     *
     * @param channel 채널 엔티티
     * @param userId 현재 사용자 ID
     * @return 상대방 사용자 ID
     * @throws NoSuchElementException 상대방을 찾을 수 없는 경우
     */
    private UUID findOtherUser(Channel channel, UUID userId) {
        return readStatusRepository.findAllByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUserId)
                .filter(id -> !id.equals(userId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Other user not found in channel " + channel.getId()));
    }

    /**
     * Private 채널에 대한 응답 DTO를 생성합니다.
     *
     * @param rs ReadStatus 정보
     * @param userId 현재 사용자 ID
     * @return 채널 응답 DTO
     */
    private ChannelResponseDto mapPrivateChannel(ReadStatus rs, UUID userId) {
        Channel ch = requireChannel(rs.getChannelId());
        UUID other = findOtherUser(ch, userId);
        return channelMapper.toDto(ch, userId, other);
    }
}
