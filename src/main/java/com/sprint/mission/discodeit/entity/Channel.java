package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id; // 채널 uuid
    private String channelName; // 채널의 이름
    private ChannelType channelType; // 채널 타입
    private final Instant createdAt;
    private Instant updatedAt;
    // 메뉴 안에서도 메세지를 입력할 수 있어야한다

    public Channel(ChannelType channelType, String channelName) {
        this.channelName = channelName;
        this.channelType = channelType;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public void update(String newChannelName) {
        if(newChannelName != null && !newChannelName.equals(this.channelName)) {
            this.channelName = newChannelName;
            this.updatedAt = Instant.now();
        }
    }

    public String toCSV() {
        return id + "," + channelName + "," + createdAt + "," + updatedAt;
    }
    
}
