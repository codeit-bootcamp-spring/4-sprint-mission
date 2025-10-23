package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor
@Getter
@Setter
public class BinaryContent extends BaseEntity {

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private byte[] bytes;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;
}
