package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

    private final MessageRepository messageRepository;

    public Boolean checkOwner(UUID messageId, Authentication authentication) {
        return messageRepository.findById(messageId)
                .map(message -> message.getAuthor().equals(authentication.getName()))
                .orElse(false);
    }
}
