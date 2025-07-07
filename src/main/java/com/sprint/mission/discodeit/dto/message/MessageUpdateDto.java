package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageUpdateDto {
    @NotNull
    private final UUID id;
    @NotBlank
    private final String content;
    private final List<BinaryContentCreateDto> attachments;
}
