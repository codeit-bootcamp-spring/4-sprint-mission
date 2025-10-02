package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper (
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
)
public abstract class ChannelMapper {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "name", source = "request.name")                  // request.name -> Channel.name
    @Mapping(target = "description", source = "request.description")    // request.description -> Channel.description
    @Mapping(target = "type", source = "channelType")           //channelType -> Channel.type
    public abstract Channel toEntity(ChannelCreateServiceRequest request, ChannelType channelType);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "type", source = "channelType")           //channelType -> Channel.type
    public abstract Channel toEntity(PrivateChannelCreateServiceRequest request, ChannelType channelType);


    @Mapping(target = "participants", expression = "java(getUserResponses(channel))")
    @Mapping(target = "lastMessageAt", expression = "java(getLastReadAt(channel))")
    public abstract ChannelResponse toResponse(Channel channel);

    public Instant getLastReadAt(Channel channel) {
        return messageRepository.findAllByChannelId(channel.getId())
                .stream()
                .max(Comparator.comparing(BaseEntity::getCreatedAt))
                .map(BaseEntity::getCreatedAt)
                .orElse(null);
    }

    public List<UserResponse> getUserResponses(Channel channel) {
        return readStatusRepository.findReadStatusByChannelId(channel.getId())
                .stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toResponse)
                .toList();
    }
}
