package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-02T14:30:11+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class ReadStatusMapperImpl implements ReadStatusMapper {

    @Override
    public ReadStatus toEntity(ReadStatusCreateServiceRequest readStatusCreateServiceRequest, User user, Channel channel) {
        if ( readStatusCreateServiceRequest == null && user == null && channel == null ) {
            return null;
        }

        ReadStatus.ReadStatusBuilder readStatus = ReadStatus.builder();

        readStatus.user( user );
        readStatus.channel( channel );

        return readStatus.build();
    }

    @Override
    public ReadStatusResponse toResponse(ReadStatus readStatus) {
        if ( readStatus == null ) {
            return null;
        }

        ReadStatusResponse.ReadStatusResponseBuilder readStatusResponse = ReadStatusResponse.builder();

        readStatusResponse.id( readStatus.getId() );
        readStatusResponse.channelId( readStatus.getChannelId() );
        readStatusResponse.userId( readStatus.getUserId() );
        readStatusResponse.lastReadAt( readStatus.getLastReadAt() );

        return readStatusResponse.build();
    }
}
