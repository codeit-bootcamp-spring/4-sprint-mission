package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring",uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

    @Mapping(source = "channel.id", target = "channelId")
    MessageDto toDto(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", ignore = true) // 서비스 계층에서 setAuthor 해야 함
    @Mapping(target = "channel", ignore = true) // 서비스 계층에서 setChannel 해야 함
    @Mapping(target = "attachments", ignore = true) // 나중에 설정
    Message toEntity(MessageCreateRequest request, List<BinaryContentCreateRequest> attachmentRequests);

    @Mapping(target = "content", source = "newContent")
    void updateFromRequest(MessageUpdateRequest request, @MappingTarget Message message);
}
