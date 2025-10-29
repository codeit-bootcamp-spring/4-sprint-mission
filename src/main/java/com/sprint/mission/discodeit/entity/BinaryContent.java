package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity {

    @Column(name = "bytes", nullable = false)
    private byte[] bytes;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

    @Builder
    @JsonCreator
    public BinaryContent(
                         @JsonProperty("fileName") String fileName,
                         @JsonProperty("contentType") String contentType,
                         @JsonProperty("size") Long size,
                         @JsonProperty("data") byte[] bytes) {
        this.bytes = bytes;
        this.contentType = contentType;
        this.size = size;
        this.fileName = fileName;
    }

    public void updateStatus(BinaryContentStatus binaryContentStatus) {
        this.status = binaryContentStatus;
    }
}
