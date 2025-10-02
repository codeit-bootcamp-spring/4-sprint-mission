package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReadStatusMapper {

    @Mapping(source = "user", target = "user")          // User -> ReadStatus.user
    @Mapping(source = "channel", target = "channel")    // Channel -> ReadStatus.channel
    ReadStatus toEntity(ReadStatusCreateServiceRequest readStatusCreateServiceRequest, User user, Channel channel);

    ReadStatusResponse toResponse(ReadStatus readStatus);
}
