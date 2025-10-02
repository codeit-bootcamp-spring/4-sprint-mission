package com.sprint.mission.discodeit.dto.channel.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelCreateRequest {

    @Length(min = 1 , max = 100, message = "채널 이름은 1 ~ 100자 이어야함")
    @NotBlank(message = "채널 이름은 null이거나 공백이면 안됨")
    private String name;

    @Length(min = 1, max = 500 , message = "채널 설명은 1 ~ 500자 이어야함")
    @NotBlank(message = "채널 설명은 null이면 안됨")
    private String description;

    public ChannelCreateServiceRequest toServiceRequest() {
        return ChannelCreateServiceRequest.builder()
                .name(name)
                .description(description)
                .build();
    }
}
