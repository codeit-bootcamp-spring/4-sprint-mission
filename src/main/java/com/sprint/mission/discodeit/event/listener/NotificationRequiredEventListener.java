//package com.sprint.mission.discodeit.event.listener;
//
//import com.sprint.mission.discodeit.event.MessageCreatedEvent;
//import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
//import com.sprint.mission.discodeit.service.NotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationRequiredEventListener {
//
//    private final NotificationService notificationService;
//
//    @Async
//    @TransactionalEventListener
//    public void on(MessageCreatedEvent event) {
//        notificationService.create(event.message());
//    }
//
//    @Async
//    @TransactionalEventListener
//    public void on(RoleUpdatedEvent event) {
//        notificationService.create(event.user(), event.oldRole(), event.newRole());
//    }
//}
