package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Component
public class OptionalBinaryContentMapper {

    public Optional<BinaryContentCreateRequest> toBinaryContentCreateRequest(MultipartFile profile) {

        return Optional.ofNullable(profile)
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return new BinaryContentCreateRequest(
                                file.getOriginalFilename(),
                                file.getContentType(),
                                file.getBytes() //반드시 예외처리 필요한 메서드
                        );
                    } catch (IOException e) {
                        throw new RuntimeException("파일 변환 중 오류 발생: " + file.getOriginalFilename(), e);
                    }
                });
    }
}
