package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-02T14:30:10+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Message toEntity(MessageCreateServiceRequest request, User user, Channel channel) {
        if ( request == null && user == null && channel == null ) {
            return null;
        }

        Message.MessageBuilder message = Message.builder();

        if ( request != null ) {
            message.content( request.getMessage() );
        }
        message.author( user );
        message.channel( channel );

        return message.build();
    }

    @Override
    public MessageResponse toResponse(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageResponse.MessageResponseBuilder messageResponse = MessageResponse.builder();

        messageResponse.channelId( messageChannelId( message ) );
        messageResponse.id( message.getId() );
        messageResponse.createdAt( message.getCreatedAt() );
        messageResponse.updatedAt( message.getUpdatedAt() );
        messageResponse.content( message.getContent() );
        messageResponse.author( userMapper.toResponse( message.getAuthor() ) );

        messageResponse.attachments( map(message.getAttachments()) );

        return messageResponse.build();
    }

    private UUID messageChannelId(Message message) {
        if ( message == null ) {
            return null;
        }
        Channel channel = message.getChannel();
        if ( channel == null ) {
            return null;
        }
        UUID id = channel.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
