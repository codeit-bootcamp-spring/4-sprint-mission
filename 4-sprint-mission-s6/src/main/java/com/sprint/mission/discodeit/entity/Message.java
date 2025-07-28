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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseUpdatableEntity {

  //
  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;
  //
  /*Channel 1 : N Message*/
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  /*User 1 : N Message (작성자)*/
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  /*1(Message) : N(BinaryContent)*/
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(name = "message_attachment", joinColumns = @JoinColumn(name = "message_id", nullable = false),
  inverseJoinColumns = @JoinColumn(name = "binary_content_id", nullable = false))
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author) {
    //
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}
