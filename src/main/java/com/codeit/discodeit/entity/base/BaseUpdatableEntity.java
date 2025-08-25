package com.codeit.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)  // Auditing
public class BaseUpdatableEntity extends BaseEntity {
  @LastModifiedDate
  @Column(name="updatedAt", nullable = false)
  private Instant updatedAt;
}
