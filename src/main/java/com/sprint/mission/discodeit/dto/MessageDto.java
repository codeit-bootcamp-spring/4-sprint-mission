package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.validator.NoEmptyMultipartFile;
import com.sprint.mission.discodeit.validator.RequiredListIfPresent;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {

    @Getter
    @AllArgsConstructor
    public static class create {
        @NotBlank
        private String content;
        @ValidUUID
        private UUID channelId;
        @ValidUUID
        private UUID authorId;
        @RequiredListIfPresent
        @Valid
        private List<@NoEmptyMultipartFile MultipartFile> files;


        public Message toMessage(List<UUID> attachmentIds) {
            return new Message(
                    content,
                    channelId,
                    authorId,
                    attachmentIds
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class update {
        @NotBlank
        private String newContent;
    }

    public record response(
            UUID id,
            String content,
            UUID channelId,
            UUID authorId,
            Instant createdAt,
            Instant updatedAt,
            List<UUID> attachmentIds
    ) {
    }
}
