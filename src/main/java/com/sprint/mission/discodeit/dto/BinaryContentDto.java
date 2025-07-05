package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.validator.NoEmptyList;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BinaryContentDto {

    @AllArgsConstructor
    @Getter
    public static class create {
        @Nullable
        private MultipartFile file;
    }

    @AllArgsConstructor
    @Getter
    public static class getBinaryContents {
        @NoEmptyList
        @Valid
        private List<@ValidUUID UUID> ids;
    }

    public record response(
            UUID id,
            String fileName,
            Long size,
            String contentType,
            Instant createdAt,
            byte[] bytes
    ) {
    }
}
