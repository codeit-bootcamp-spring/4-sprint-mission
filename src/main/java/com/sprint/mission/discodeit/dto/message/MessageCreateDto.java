package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateDto {
    @NotBlank
    private final String content;
    @NotNull
    private final UUID channelId;
    @NotNull
    private final UUID authorId;
    @Setter
    private List<BinaryContentCreateDto> attachments;
}
