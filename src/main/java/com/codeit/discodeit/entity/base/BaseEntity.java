package com.codeit.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EntityListeners;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Service;

@Setter
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // Auditing
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", unique = true, nullable = false)
    private UUID id;

    @CreatedDate
    @Column(name="createdAt", updatable = false, nullable = false)
    private Instant createdAt;
}