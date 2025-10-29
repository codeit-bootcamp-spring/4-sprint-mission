package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class BinaryContentConverter {
    public static BinaryContent toBinaryContent(MultipartFile file) throws IOException {
        return BinaryContent.builder()
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .bytes(file.getBytes())
                .size(file.getSize())
                .build();
    }
}
