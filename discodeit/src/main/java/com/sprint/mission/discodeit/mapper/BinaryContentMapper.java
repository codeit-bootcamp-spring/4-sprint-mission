package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BinaryContentMapper {
    // 1. BinaryContentRequest → BinaryContent
    public BinaryContent toEntity(UUID userId, UUID messageId, BinaryContentRequest dto) {
        return new BinaryContent(
                userId,
                messageId,
                dto.bytes(),
                dto.fileName(),
                dto.contentType()
        );
    }

    // 2. BinaryContent → BinaryContentResponse
    public BinaryContentResponse toResponse(BinaryContent entity) {
        return new BinaryContentResponse(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getFileName(),
                entity.getDatas() != null ? (long) entity.getDatas().length : 0L,
                entity.getContentType(),
                entity.getDatas() != null ? Base64.getEncoder().encodeToString(entity.getDatas()) : null
        );
    }

    // 3. List<BinaryContent> → ResponseDtos
    public ResponseDtos toResponses(List<BinaryContent> contents) {
        List<BinaryContentResponse> responses = contents.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new ResponseDtos(responses);
    }
}
