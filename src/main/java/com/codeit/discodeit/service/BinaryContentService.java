package com.codeit.discodeit.service;

import com.codeit.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {


  List<BinaryContent> findBinaryContentsByBinaryContentIds(List<UUID> binaryContentIds);

  BinaryContent findBinaryContentByBinaryContentId(UUID binaryContentId);
}
