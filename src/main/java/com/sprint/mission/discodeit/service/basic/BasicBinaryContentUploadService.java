package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentUploadService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentUploadService implements BinaryContentUploadService {

    private final BinaryContentStorage storage;
    private final ApplicationEventPublisher publisher;
    private static final String TASK_NAME = "S3 파일 업로드";

    @Retryable(
            retryFor = {S3Exception.class, RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000,
                    multiplier = 2)
    )
    @Override
    public void upload(UUID binaryContentId, byte[] bytes) {
        log.debug("S3파일 업로드 시작: id={}", binaryContentId);
        storage.put(binaryContentId, bytes);
    }


    @Override
    @Recover
    public void recover(RuntimeException e, UUID binaryContentId, byte[] bytes) {
        String requestId = MDC.get("requestId");
        String errorMessage = getRootCauseMessage(e);

        log.error("S3 저장 실패(모든 재시도 실패) - TaskName: {}, RequestId: {}, BinaryContentId: {},Error: {}",
                TASK_NAME,
                requestId,
                binaryContentId,
                errorMessage,
                e);

        // 관리자에게 실패 알람 생성
        publisher.publishEvent(new S3UploadFailedEvent(binaryContentId, requestId, TASK_NAME, errorMessage));

        throw e;
    }

    private String getRootCauseMessage(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }
}
