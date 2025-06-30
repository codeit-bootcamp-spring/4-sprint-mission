package com.sprint.mission.discodeit.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreateDto {
    @NotBlank
    String content;
    @NotNull
    UUID channelId;
    @NotNull
    UUID authorId;
    List<UUID> attachmentIds;
}
