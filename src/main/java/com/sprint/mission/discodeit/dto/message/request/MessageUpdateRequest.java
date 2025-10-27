package com.sprint.mission.discodeit.dto.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageUpdateRequest {

    @NotBlank(message = "메시지는 null이거나 빈문자열, 공백이면 안됨")
    private String newContent;

    @NotNull(message = "채널ID는 null이면 안됨")
    private UUID channelId;

    @NotNull(message = "유저ID는 null이면 안됨")
    private UUID userId;

    public MessageUpdateServiceRequest toServiceRequest(UUID messageId) {
        return MessageUpdateServiceRequest.builder()
                .messageId(messageId)
                .content(newContent)
                .channelId(channelId)
                .userId(userId)
                .build();
    }
}
