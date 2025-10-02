package com.sprint.mission.discodeit.dto.readstatus.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 추후 도메인 객체에 업데이트 할만한 필드 추가되면 같이 추가할 것
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadStatusUpdateRequest {

    @NotNull(message = "읽음 상태가 null 이면 안됨")
    private Instant newLastReadAt;

    public ReadStatusUpdateServiceRequest toServiceRequest(UUID readStatusId) {
        return ReadStatusUpdateServiceRequest.builder()
                .newLastReadAt(newLastReadAt)
                .readStatusId(readStatusId)
                .build();
    }
}
