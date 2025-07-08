package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record PrivateChannelRequest(
    // List가 여기에 왜 들어가는거지?.. 비밀채널을 만들 때 들어갈 사람을 미리 선별?? 해서 가는건가..
    List<UUID> participantIds
) {}

