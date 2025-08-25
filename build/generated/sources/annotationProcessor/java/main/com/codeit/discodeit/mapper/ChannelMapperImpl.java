package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-14T14:47:04+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class ChannelMapperImpl implements ChannelMapper {

    @Override
    public Channel toPublicChannel(CreatePublicChannelRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Channel.ChannelBuilder channel = Channel.builder();

        channel.name( dto.getName() );
        channel.description( dto.getDescription() );

        channel.type( ChannelType.PUBLIC );

        return channel.build();
    }
}
