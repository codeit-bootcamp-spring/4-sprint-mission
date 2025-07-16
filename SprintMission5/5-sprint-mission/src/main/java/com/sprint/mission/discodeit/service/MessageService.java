package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.Message;
import com.sprint.mission.discodeit.dto.message.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import com.sprint.mission.discodeit.dto.message.MessageUpdateServiceRequest;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  Message createMessage(MessageCreateServiceRequest messageCreateServiceRequest);

  public Message updateMessage(UUID messageId, MessageUpdateServiceRequest request);

  void deleteMessage(UUID messageId);

  List<Message> findAllByChannelId(UUID channelId);

}
