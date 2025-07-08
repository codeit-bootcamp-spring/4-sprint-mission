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
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id; // 이 id는 메세지의 id이다
    private final UUID channelId; // 메세지가 있는 채널
    private String content; // 메세지 내용
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId; // 이건 작성자의 id이다


    public Message(String content, UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.content = content;
        this.createdAt = Instant.now();

        this.userId = userId;
    }

    public void update(String newContent) { // 메세지 내용 수정하기
        if(newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            updatedAt = Instant.now();
        }
    }


    public String toCSV() {
        return id + "," + channelId + "," + content + "," + createdAt + "," + updatedAt;
    }

}
