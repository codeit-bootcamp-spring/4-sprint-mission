package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "channels")
@Getter
@Setter
@NoArgsConstructor
public class Channel extends BaseUpdateEntity{

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(length = 100)
    private String name;

    @Column(length = 500)
    private String description;

//    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<Message> messageList = new ArrayList<>();
//
//    public void addMessage(Message message) {
//        messageList.add(message);
//        if (message != null && message.getChannel() != this) {
//            message.setChannel(this);
//        }
//    }

//    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<ReadStatus> readStatusList = new ArrayList<>();
//
//    public void addReadStatus(ReadStatus readStatus) {
//        readStatusList.add(readStatus);
//        if (readStatus != null && readStatus.getChannel() != this) {
//            readStatus.setChannel(this);
//        }
//    }

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }

    }
}
