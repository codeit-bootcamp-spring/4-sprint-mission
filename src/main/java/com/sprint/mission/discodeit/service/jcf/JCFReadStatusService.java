package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JCFReadStatusService implements ReadStatusService {
    @Override
    public ReadStatusDto createReadStatus(ReadStatusRequest readStatusRequest) {
        return null;
    }

    @Override
    public List<ReadStatusDto> findReadStatus(UUID readStatusId) {
        return null;
    }

    @Override
    public List<ReadStatusDto> finaAllReadStatus() {
        return List.of();
    }

    @Override
    public ReadStatusDto updateReadStatus(UUID id, ReadStatusRequest readStatusRequest) {
        return null;
    }

    @Override
    public void deleteReadStatus(UUID id) {

    }
}
