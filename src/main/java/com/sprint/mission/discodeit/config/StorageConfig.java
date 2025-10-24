package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
    // 로컬 저장소
    @Bean
    @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local", matchIfMissing = true)
    public BinaryContentStorage localBinaryContentStorage() {
        return new LocalBinaryContentStorage();
    }

    // S3 저장소
    @Bean
    @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
    public BinaryContentStorage s3BinaryContentStorage(@Value("${discodeit.storage.s3.access-key}")
                                                       String accessKey,
                                                       @Value("${discodeit.storage.s3.secret-key}")
                                                       String secretKey,
                                                       @Value("${discodeit.storage.s3.region}")
                                                       String region,
                                                       @Value("${discodeit.storage.s3.bucket}")
                                                       String bucket,
                                                       BinaryContentService binaryContentService,
                                                       ApplicationEventPublisher applicationEventPublisher) {
        return new S3BinaryContentStorage(accessKey, secretKey, region, bucket, binaryContentService, applicationEventPublisher);
    }
}
