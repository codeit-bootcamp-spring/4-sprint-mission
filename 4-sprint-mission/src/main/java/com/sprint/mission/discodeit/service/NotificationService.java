package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<Notification> create(Message message);

    Notification create(User user, Role oldRole, Role newRole);

    void create(DiscodeitException e);

    List<Notification> findAllByUserId(UUID userId);

    void delete(User user, UUID id);

}
