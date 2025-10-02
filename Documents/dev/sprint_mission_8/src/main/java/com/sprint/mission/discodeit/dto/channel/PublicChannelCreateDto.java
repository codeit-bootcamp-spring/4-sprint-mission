package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PublicChannelCreateDto {
    private ChannelType type;

    @NotBlank(message = "채널명은 필수로 입력해주세요.")
    @Size(min = 2, max = 20, message = "채널명은 2~20자 사이여야 합니다.")
    private String name;

    @Size(min = 2, max = 100, message = "채널 소개는 2~100자 사이여야 합니다.")
    private String description;

    private List<UUID> participants;
}
