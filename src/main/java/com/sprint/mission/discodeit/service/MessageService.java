package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
=======
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
>>>>>>> fe56ded0b57ffcf4521001ca7a956c8f5baf981f

import java.util.List;
import java.util.UUID;

public interface MessageService {
<<<<<<< HEAD

    MessageResponseDto create(MessageCreateDto messageCreateDto);

    List<MessageResponseDto> findByChannelId(UUID channelId);

    Message update(UUID messageId, String newContent);

    void delete(UUID messageId);
}
=======
    Message createMessage(Message create, User user, Channel channel);
    Message getMessage(UUID id);
    List<Message> getMessages();
    void updateMessage (UUID id, String message);
    boolean deleteMessage (UUID id);

}
>>>>>>> fe56ded0b57ffcf4521001ca7a956c8f5baf981f
