//package com.sprint.mission.discodeit.event.Listener;
//
//import com.amazonaws.AmazonServiceException;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.Notification;
//import com.sprint.mission.discodeit.entity.ReadStatus;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.event.BinaryContentRecoverEvent;
//import com.sprint.mission.discodeit.event.MessageCreatedEvent;
//import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
//import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
//import com.sprint.mission.discodeit.repository.NotificationRepository;
//import com.sprint.mission.discodeit.repository.ReadStatusRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.MDC;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//import software.amazon.awssdk.services.s3.model.S3Exception;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class NotificationRequiredEventListener {
//
//  private final ReadStatusRepository readStatusRepository;
//  private final NotificationRepository notificationRepository;
//
//  private final UserRepository userRepository;
//
//  @Async // 이벤트 리스너를 비동기적으로 실행
//  @Transactional(propagation = Propagation.REQUIRES_NEW)
//  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//  //evict the cache for all users who might have been affected
//  @CacheEvict(value = "userNotificationsCache", allEntries = true)
//  public void on(MessageCreatedEvent event) {
//
//    try {
//      log.info("MessageCreatedEvent 수신 성공 - 알림 생성 시작");
//
//      Message message = event.message();
//      Channel channel = message.getChannel();
//      User user = message.getAuthor();
//
//      List<ReadStatus> readStatusList = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(
//          channel.getId());
//
//      log.info("조회된 ReadStatus 개수: {}", readStatusList.size());
//
//      List<Notification> notifications = readStatusList.stream()
//          // 해당 메시지를 보낸 사람은 알림 대상에서 제외
//          .filter(r -> !r.getUser().getId().equals(user.getId()))
//          .map(r ->
//              new Notification(
//                  r.getUser(),
//                  String.format("%s (#%s)", user.getUsername(), channel.getName()),
//                  message.getContent()
//              )
//          ).toList();
//
//      List<Notification> list = notificationRepository.saveAll(notifications);
//      log.info("MessageCreatedEvent 수신 완료. list={}", list);
//    } catch (Exception e) {
//      log.error("Failed to create notifications for MessageCreatedEvent, ", e);
//    } finally {
//      log.info("userNotificationsCache cleared");
//    }
//  }
//
//  @Async
//  @Transactional(propagation = Propagation.REQUIRES_NEW)
//  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//  @CacheEvict(value = "userNotificationsCache", key = "#event.user().id")
//  public void on(RoleUpdatedEvent event) {
//    log.info("RoleUpdatedEvent 수신 성공 - 알림 생성 시작");
//
//    User user = event.user();
//    Notification notification = new Notification(
//        user,
//        "권한이 변경되었습니다.",
//        String.format("%s -> %s", event.role(), user.getRole())
//    );
//    Notification savedNotification = notificationRepository.save(notification);
//    log.info("RoleUpdatedEvent 수신 완료. savedNotification={}", savedNotification);
//  }
//
//  @Async
//  @Transactional(propagation = Propagation.REQUIRES_NEW)
//  @EventListener
//  public void on(BinaryContentRecoverEvent event) {
//    log.info("BinaryContentRecoverEvent 수신 성공 - 알림 생성 시작");
//
//    S3Exception s3Event = event.e();
//
//    User adminUser = userRepository.findByUsername("admin")
//        .orElseThrow(UserNotFoundException::new);
//
//    Notification notification = new Notification(
//        adminUser,
//        "S3 파일 업로드 실패",
//        String.format("RequestId: %s, "
//                + "BinaryContentId: %s, "
//                + "Error: %s",
//            // MDC의 Request Id
//            MDC.get("REQUEST_ID"),
//            event.binaryContentId(),
//            s3Event != null ? s3Event.getMessage() : "AWS S3 파일 업로드 오류"
//        )
//    );
//    Notification savedNotification = notificationRepository.save(notification);
//    log.info("BinaryContentRecoverEvent 수신 완료. savedNotification={}", savedNotification);
//  }
//}
