package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;

import java.time.Instant;

@Mapper(componentModel = "spring")

public interface ReadStatusMapper {
    default ReadStatus toEntity(User user, Channel channel, Instant lastReadAt) {
        ReadStatus readStatus = new ReadStatus();
        readStatus.setUser(user);
        readStatus.setChannel(channel);
        readStatus.setLastReadAt(lastReadAt);
        return readStatus;
    }

    ReadStatusDto readStatusToReadStatusDto(ReadStatus readStatus);

//    public ReadStatus readStatusCreateDtoToReadStatus(ReadStatusCreateDto dto) {
//        return new ReadStatus(
//                dto.getUserId(),
//                dto.getChannelId(),
//                dto.getReadAt()
//        );
//    }

//    public ReadStatusResponseDto readStatusToReadStatusResponseDto(ReadStatus readStatus) {
//        return new ReadStatusResponseDto(
//                readStatus.getUserId(),
//                readStatus.getChannelId(),
//                readStatus.getReadAt(),
//                readStatus.getCreatedAt(),
//                readStatus.getUpdatedAt()
//        );
//    }
}
