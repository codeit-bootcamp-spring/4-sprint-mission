package com.codeit.discodeit8.repository;

import com.codeit.discodeit8.entity.BinaryContent;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

}