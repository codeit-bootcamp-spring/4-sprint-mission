package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.readstatus_dto.ReadStatusDto;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-14T14:47:04+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class ReadStatusMapperImpl implements ReadStatusMapper {

    @Override
    public ReadStatusDto toReadStatusDto(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }

        UUID userId = null;
        UUID channelId = null;
        UUID id = null;
        Instant lastReadAt = null;

        userId = readStatusUserId( readStatus );
        channelId = readStatusChannelId( readStatus );
        id = readStatus.getId();
        lastReadAt = readStatus.getLastReadAt();

        ReadStatusDto readStatusDto = new ReadStatusDto( id, userId, channelId, lastReadAt );

        return readStatusDto;
    }

    private UUID readStatusUserId(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }
        User user = readStatus.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID readStatusChannelId(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }
        Channel channel = readStatus.getChannel();
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
