package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentSaveFailEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveFailException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.BinaryContentStorageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicBinaryContentStorageService implements BinaryContentStorageService {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;

    @Override
    @Retryable(
            value = {BinaryContentSaveFailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2) // 재시도 간격: 1초부터 2배씩 증가(1s, 2s, 4s, ...)
    )
    public void save(BinaryContentCreatedEvent event) {
        log.debug("### BasicBinaryContentStorageService.save()");
        UUID id = binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
        try {
            if (!id.equals(event.getBinaryContentId())) {
                binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
                log.error("### 바이너리 데이터 저장 실패, status=FAIL");
                throw BinaryContentSaveFailException.withId(event.getBinaryContentId());
            }
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.SUCCESS);
            log.info("# 바이너리 데이터 저장 성공, status=SUCCESS");
        } catch (Exception e) {
            log.error("### BasicBinaryContentStorageService.save()에서 exception 발생", e);
            binaryContentService.updateStatus(event.getBinaryContentId(), BinaryContentStatus.FAIL);
            throw BinaryContentSaveFailException.withId(event.getBinaryContentId());
        }
    }

    @Recover
    @Transactional
    @Override
    public void recover(BinaryContentSaveFailException e,
                        BinaryContentCreatedEvent event) {
        log.error("### 모든 재시도 정책 실패");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User sender = null;
        if (auth != null && auth.getPrincipal() instanceof DiscodeitUserDetails dud) {
            String userName = dud.getUsername();
            sender = userRepository.findByUsername(userName)
                    .orElseThrow(() -> UserNotFoundException.withUsername(userName));
        }

        publisher.publishEvent(new BinaryContentSaveFailEvent(
                event.getBinaryContentId(),
                e.getMessage(),
                sender != null ? sender.getId() : null
        ));
    }
}
