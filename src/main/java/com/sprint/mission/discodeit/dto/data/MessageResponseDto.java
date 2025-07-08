package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record MessageResponseDto( // DTO는 연결해주는 느낌이 짙다
                                  // Controller 및 Service에게만 넘겨줘야 할 정보들
                                  // 화면에 출력되어도 의미없거나 중요한 정보들은 적지 않기
                                  String content, // 메시지 내용
                                  UUID messageId, // 메시지의 UID

                                  UUID userId, // 메시지를 작성한 유저의 UID
                                  UUID channelId // 메시지가 작성된 채널의 UID
) {
}
