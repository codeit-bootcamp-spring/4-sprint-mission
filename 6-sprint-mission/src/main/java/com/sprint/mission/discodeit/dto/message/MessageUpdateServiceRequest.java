package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record MessageUpdateServiceRequest(String newContent, List<MultipartFile> image) {}
