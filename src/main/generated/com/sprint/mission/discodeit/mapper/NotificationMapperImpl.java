package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-04T10:29:17+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        UUID id = null;
        Instant createdAt = null;
        UUID receiverId = null;
        String title = null;
        String content = null;

        id = notification.getId();
        createdAt = notification.getCreatedAt();
        receiverId = notification.getReceiverId();
        title = notification.getTitle();
        content = notification.getContent();

        NotificationDto notificationDto = new NotificationDto( id, createdAt, receiverId, title, content );

        return notificationDto;
    }
}
