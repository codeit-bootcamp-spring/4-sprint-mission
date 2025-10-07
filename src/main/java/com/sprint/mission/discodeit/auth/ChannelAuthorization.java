package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("channelAuth")
@RequiredArgsConstructor
public class ChannelAuthorization {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    public boolean canUpdateChannel(UUID channelId, DiscodeitUserDetails userDetails) {
        return channelRepository.findById(channelId)
                .map(channel -> {
                    if (channel.getType() == ChannelType.PUBLIC) {
                        return userDetails.getAuthorities().stream()
                                .anyMatch(auth -> auth.getAuthority().equals("CHANNEL_MANAGER"));
                    }
                    return true;
                })
                .orElse(false);
    }
}
