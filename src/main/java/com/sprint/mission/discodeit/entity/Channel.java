package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.ChannelException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChannelType type;

    @Builder
    private Channel(String name, String description, ChannelType type) {
        if(type == ChannelType.PUBLIC) {
            this.name = name;
            this.description = description;
            this.type = type;
        } else {
            validatePrivateChannelDoesNotHaveNameOrDescription(name, description);
            this.type = type;
        }
    }

    public void editChannelName(String channelName) {
        this.name = channelName;
    }

    public void editDescription(String description) {
        this.description = description;
    }


    private void validatePrivateChannelDoesNotHaveNameOrDescription(String name, String description) {
        if(name != null && description != null) {
            throw new ChannelException(ChannelErrorCode.PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME_AND_DESCRIPTION);
        }
        if(name != null) {
            throw new ChannelException(ChannelErrorCode.PRIVATE_CHANNEL_DOES_NOT_HAVE_NAME);
        }
        if(description!=null) {
            throw new ChannelException(ChannelErrorCode.PRIVATE_CHANNEL_DOES_NOT_HAVE_DESCRIPTION);
        }
    }
}
