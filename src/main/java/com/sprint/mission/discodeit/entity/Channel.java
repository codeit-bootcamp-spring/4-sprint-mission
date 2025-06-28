package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    //
    private ChannelType channelType;
    private String channelName;
    private String description;

    public Channel(ChannelType channelType, String channelName, String description) {
        super();
        //
        this.channelType = channelType;
        this.channelName = channelName;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + super.getId() +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                ", type=" + channelType +
                ", name='" + channelName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.channelName)) {
            this.channelName = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.ofEpochSecond(Instant.now().getEpochSecond()));
        }
    }
}
