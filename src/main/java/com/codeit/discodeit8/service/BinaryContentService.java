package com.codeit.discodeit8.service;

import com.codeit.discodeit8.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit8.entity.BinaryContent;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {

  void createByteFile(BinaryContent binaryContent, byte[] bytes);

  List<BinaryContent> findBinaryContentsByBinaryContentIds(List<UUID> binaryContentIds);

  BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId);

  ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
