package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.form.MessageForm;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class BinaryContentMapper {

    public List<BinaryContentCreateRequest> addBinaryContentCreateRequest(MessageForm form) {
        List<BinaryContentCreateRequest> requests = new ArrayList<>();

        if (form.attachments() == null || form.attachments().isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        for (MultipartFile file : form.attachments()) {
            try {
                requests.add(new BinaryContentCreateRequest(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                ));
            } catch (Exception e) {
                throw new RuntimeException("파일 변환 중 오류 발생: " + file.getOriginalFilename(), e);
            }
        }
        return requests;
    }
}

