package com.sprint.mission.discodeit.event;

import com.amazonaws.AmazonServiceException;

import java.util.UUID;
import software.amazon.awssdk.services.s3.model.S3Exception;

public record BinaryContentRecoverEvent(
    Object source,
    UUID binaryContentId,
    S3Exception e
) {

}
