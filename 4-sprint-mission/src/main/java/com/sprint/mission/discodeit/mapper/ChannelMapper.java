package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class ChannelMapper {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserMapper usermapper;

    public ChannelDto toChannelDto(Channel channel) {
        List<UserDto> participants = new ArrayList<>();
        readStatusRepository.findByChannelId(channel.getId()).stream()
                .forEach(readStatus -> participants.add(usermapper.toDto(readStatus.getUser())));
        Optional<Message> latestMessage = messageRepository.findTopByChannelOrderByUpdatedAtDesc(channel);
        Instant lastMessageAt = latestMessage.map(Message::getUpdatedAt).orElse(Instant.now());

        return ofChannelDto(channel, participants, lastMessageAt);
    }

    abstract public ChannelDto ofChannelDto(Channel channel, List<UserDto> participants, Instant lastMessageAt);
}
