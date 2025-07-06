package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MessageMapper {
    public static Message dtoToEntity (MessageCreateDto dto, List<UUID> attachmentIds) {
        Instant createAt = Instant.now();
        Instant updateAt = createAt;

        List<UUID> safeAttachmentIds = new ArrayList<>();
        if (attachmentIds != null) {
            safeAttachmentIds.addAll(attachmentIds);
        }

        return new Message(
                UUID.randomUUID(),
                dto.getAuthorId(),
                dto.getChannelId(),
                dto.getContent(),
                safeAttachmentIds,
                createAt,
                updateAt
        );
    }

    public static MessageResponseDto entityToDto (Message message, List<BinaryContentResponseDto> attachments) {
        return new MessageResponseDto(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                attachments,
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
