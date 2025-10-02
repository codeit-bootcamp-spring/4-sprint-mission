package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-02T14:30:11+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class ChannelMapperImpl extends ChannelMapper {

    @Override
    public Channel toEntity(ChannelCreateServiceRequest request, ChannelType channelType) {
        if ( request == null && channelType == null ) {
            return null;
        }

        Channel.ChannelBuilder channel = Channel.builder();

        if ( request != null ) {
            channel.name( request.getName() );
            channel.description( request.getDescription() );
        }
        channel.type( channelType );

        return channel.build();
    }

    @Override
    public Channel toEntity(PrivateChannelCreateServiceRequest request, ChannelType channelType) {
        if ( request == null && channelType == null ) {
            return null;
        }

        Channel.ChannelBuilder channel = Channel.builder();

        channel.type( channelType );

        return channel.build();
    }

    @Override
    public ChannelResponse toResponse(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        ChannelResponse.ChannelResponseBuilder channelResponse = ChannelResponse.builder();

        channelResponse.id( channel.getId() );
        channelResponse.type( channel.getType() );
        channelResponse.name( channel.getName() );
        channelResponse.description( channel.getDescription() );

        channelResponse.participants( getUserResponses(channel) );
        channelResponse.lastMessageAt( getLastReadAt(channel) );

        return channelResponse.build();
    }
}
