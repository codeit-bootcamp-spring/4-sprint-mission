package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        uses = {UserMapper.class, BinaryContentMapper.class}
)
public interface MessageMapper {

    @Mapping(target = "content", source = "request.message")  // reqeust.message -> Message.content
    @Mapping(target = "author", source = "user")  // User -> Message.author
    @Mapping(target = "channel", source = "channel") // Channel -> Message.channel
    Message toEntity(MessageCreateServiceRequest request, User user, Channel channel);


    @Mapping(target = "attachments", expression = "java(map(message.getAttachments()))")
    @Mapping(target = "channelId", source = "channel.id")
    MessageResponse toResponse(Message message);

    default List<BinaryContentResponse> map(List<MessageAttachment> messageAttachments) {
        return messageAttachments.stream()
                .map(MessageAttachment::getBinaryContent)
                .map(BinaryContentResponse::new)
                .toList();
    }

}
