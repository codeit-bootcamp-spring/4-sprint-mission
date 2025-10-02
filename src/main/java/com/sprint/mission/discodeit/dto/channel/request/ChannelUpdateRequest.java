package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelUpdateRequest {

    @Length(min = 1, max = 100, message  = "채널 이름은 1~100자 이어야 함")
    @NotBlank(message = "채널 이름은 null이거나 공백이면 안됨")
    private String newName;

    @Length(min = 1, max = 255, message = "채널 설명은 1~100자 이어야 함")
    @NotBlank(message = "채널 설명은 null이거나 공백이면 안됨")
    private String newDescription;

    public ChannelUpdateServiceRequest toServiceRequest(UUID channelId) {
        return ChannelUpdateServiceRequest.builder()
                .channelId(channelId)
                .name(newName)
                .description(newDescription)
                .build();
    }
}
