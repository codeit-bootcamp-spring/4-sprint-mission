package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-14T14:47:04+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BinaryContentMapper binaryContentMapper;

    @Override
    public MessageDto toMessageDto(Message message) {
        if ( message == null ) {
            return null;
        }

        UUID channelId = null;
        UserDto author = null;
        List<BinaryContentDto> attachments = null;
        UUID id = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        String content = null;

        channelId = messageChannelId( message );
        author = userMapper.toUserDto( message.getAuthor() );
        attachments = binaryContentListToBinaryContentDtoList( message.getAttachments() );
        id = message.getId();
        createdAt = message.getCreatedAt();
        updatedAt = message.getUpdatedAt();
        content = message.getContent();

        MessageDto messageDto = new MessageDto( id, createdAt, updatedAt, content, channelId, author, attachments );

        return messageDto;
    }

    @Override
    public Message toMessage(String content, User user, Channel channel, List<BinaryContent> attachments) {
        if ( content == null && user == null && channel == null && attachments == null ) {
            return null;
        }

        Message.MessageBuilder message = Message.builder();

        message.content( content );
        message.author( user );
        message.channel( channel );
        List<BinaryContent> list = attachments;
        if ( list != null ) {
            message.attachments( new ArrayList<BinaryContent>( list ) );
        }

        return message.build();
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

    protected List<BinaryContentDto> binaryContentListToBinaryContentDtoList(List<BinaryContent> list) {
        if ( list == null ) {
            return null;
        }

        List<BinaryContentDto> list1 = new ArrayList<BinaryContentDto>( list.size() );
        for ( BinaryContent binaryContent : list ) {
            list1.add( binaryContentMapper.toBinaryContentDto( binaryContent ) );
        }

        return list1;
    }
}
