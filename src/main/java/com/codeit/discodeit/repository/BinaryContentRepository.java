package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.BinaryContent;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
}