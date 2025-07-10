package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JCFMessageRepository implements MessageRepository {

  /**
   * channelMsgIndex: Channel Id를 key로 사용하여 O(1) 시간에 채널의 메세지 ID 목록을 조회합니다. Key: UUID ChannelId,
   * Value: List<UUID> messageUUIDs 채널 아이디로 조회할때 O(n)에서 O(1) 시간 복잡도로 줄일 수 있어서 구현했습니다.
   */
  private final Map<UUID, Message> data; // Message Id : Message
  private final Map<UUID, List<UUID>> channelMsgIndex; // Channel Id : List<UUID>

  public JCFMessageRepository() {
    this.data = new HashMap<>();
    this.channelMsgIndex = new HashMap<>();
  }

  @Override
  public Message save(Message message) {
    this.data.put(message.getId(), message);

    List<UUID> messageIdsForChannel = this.channelMsgIndex.computeIfAbsent(
        message.getChannelId(),
        k -> new ArrayList<>()
    );

    // add only when message doesn't exist
    if (!messageIdsForChannel.contains(message.getId())) {
      messageIdsForChannel.add(message.getId());
    }

    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {

    return Optional.ofNullable(this.data.get(id));
  }

  @Override
  public List<Message> findByChannelId(UUID channelId) {
    return this.data.values().stream().filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public List<Message> findAll() {
    return new ArrayList<>(this.data.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return this.data.containsKey(id);
  }

  @Override
  public void deleteById(UUID messageId) {

    Message deletedMessage = this.data.remove(messageId);

    if (deletedMessage != null) {
      List<UUID> channelMessageList = channelMsgIndex.get(deletedMessage.getChannelId());
      if (channelMessageList != null) {
        channelMessageList.remove(deletedMessage.getId());
        if (channelMessageList.isEmpty()) {
          channelMsgIndex.remove(deletedMessage.getChannelId());
        }
      }
    }
  }

  @Override
  public void deleteAll() {
    this.data.clear();
    this.channelMsgIndex.clear();
  }


  private List<Message> toMessageList(List<UUID> messageUUIDList) {
    return messageUUIDList.stream()
        .map(this::findById)
        .flatMap(Optional::stream) // Stream<Optional<T>> -> Stream<T>
        .toList();
  }
}
