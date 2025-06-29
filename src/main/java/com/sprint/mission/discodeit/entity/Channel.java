package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.ChannelUpdateRequestDto;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(ChannelUpdateRequestDto channelUpdateRequestDto) {
        boolean anyValueUpdated = false;
        if (channelUpdateRequestDto.getName() != null && !channelUpdateRequestDto.getName().equals(this.name)) {
            this.name = channelUpdateRequestDto.getName();
            anyValueUpdated = true;
        }
        if (channelUpdateRequestDto.getDescription() != null && !channelUpdateRequestDto.getDescription().equals(this.description)) {
            this.description = channelUpdateRequestDto.getDescription();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
