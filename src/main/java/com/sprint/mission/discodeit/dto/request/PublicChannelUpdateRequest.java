package com.sprint.mission.discodeit.dto.request;

import org.hibernate.validator.constraints.Length;

public record PublicChannelUpdateRequest(
        @Length(max = 100)
        String newName,

        @Length(max = 500)
        String newDescription
) {

}
