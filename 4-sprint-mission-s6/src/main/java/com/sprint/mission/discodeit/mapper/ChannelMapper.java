package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChannelMapper {

    // ─────── Channel → ChannelDto ───────
    @Mapping(target = "participantIds", source = "channel", qualifiedByName = "mapParticipantIds")
    @Mapping(target = "lastMessageAt", source = "channel", qualifiedByName = "mapLastMessageAt")
    ChannelDto toDto(Channel channel,
                     @Context MessageRepository messageRepository,
                     @Context ReadStatusRepository readStatusRepository);

    // ─────── Public 채널 생성 ───────
    @Mapping(target = "type", constant = "PUBLIC")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Channel fromPublicCreateRequest(PublicChannelCreateRequest request);

    // ─────── Private 채널 생성 ───────
    @Mapping(target = "type", constant = "PRIVATE")
    @Mapping(target = "name", constant = "")
    @Mapping(target = "description", constant = "")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Channel fromPrivateCreateRequest(PrivateChannelCreateRequest request);

    // ─────── Public 채널 업데이트 ───────
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", source = "newName")
    @Mapping(target = "description", source = "newDescription")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    void updateFromPublicUpdateRequest(PublicChannelUpdateRequest request, @MappingTarget Channel channel);

    // ─────── @Named 헬퍼 메서드 ───────

    @Named("mapParticipantIds")
    default List<UUID> mapParticipantIds(Channel channel, @Context ReadStatusRepository readStatusRepository) {
        return readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(readStatus -> readStatus.getUser().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    @Named("mapLastMessageAt")
    default Instant mapLastMessageAt(Channel channel, @Context MessageRepository messageRepository) {
        return messageRepository.findAllByChannelId(channel.getId()).stream()
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }
}
