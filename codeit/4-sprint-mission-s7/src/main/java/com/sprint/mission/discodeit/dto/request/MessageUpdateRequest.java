package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(

    @Size(max = 5000, message = "메시지 내용은 최대 5000자까지 허용됩니다")
    String newContent
) {

}
