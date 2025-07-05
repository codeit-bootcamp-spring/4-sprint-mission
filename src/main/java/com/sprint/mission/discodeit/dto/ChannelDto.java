package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.validator.NoBlankIfPresent;
import com.sprint.mission.discodeit.validator.NoEmptyList;
import com.sprint.mission.discodeit.validator.ValidUUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {

    @Getter
    @AllArgsConstructor
    public static class createPrivateChannel {
        @NoEmptyList
        @Valid
        private List<@ValidUUID UUID> participantIds;

        public Channel toPrivateChannel() {
            return new Channel(
                    ChannelType.PRIVATE,
                    "private",
                    "private channel"
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class createPublicChannel {
        @NotBlank
        private String name;
        @NoBlankIfPresent
        private String description;

        public Channel toPublicChannel() {
            return new Channel(
                    ChannelType.PUBLIC,
                    name,
                    description
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class updatePublicChannel {
        @NoBlankIfPresent
        private String newName;
        @NoBlankIfPresent
        private String newDescription;
    }

    public record response(
            UUID id,
            ChannelType type,
            String name,
            String description,
            List<UUID> participantIds,
            Instant lastMessageAt
    ) {
    }
}
