package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.DTO.ReadStatusResponse;
import com.sprint.mission.discodeit.DTO.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    ReadStatusRepository readStatusRepository;
    ChannelRepository channelRepository;
    UserRepository userRepository;

    private ReadStatusResponse toDTO(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getCreatedAt(),
                readStatus.getUpdatedAt(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {

        if(!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if(!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found with id " + request.userId());
        }

        List<ReadStatus> existingStatuses = readStatusRepository.findByUserId(request.userId());
        boolean alreadyExists = existingStatuses.stream()
                .anyMatch(rs -> rs.getChannelId().equals(request.channelId()));
        if (alreadyExists) {
            throw new IllegalStateException("ReadStatus already exists for this user and channel");
        }

        ReadStatus readStatus = new ReadStatus(request);
        readStatusRepository.save(readStatus);
        return toDTO(readStatus);
    };

    @Override
    public ReadStatusResponse find(UUID id){
        return toDTO(readStatusRepository.findById(id));
    };

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId){
        return readStatusRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .toList();
    };

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusRepository.findByUserId(request.userId()).stream()
                .filter(rs -> rs.getChannelId().equals(request.channelId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + request.channelId()));

        readStatus.updateLastReadAt(request.lastReadAt());
        readStatusRepository.save(readStatus);
        return toDTO(readStatus);
    };

    @Override
    public void delete(UUID id){
        ReadStatus readStatus = readStatusRepository.findAll().stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id " + id));

        readStatusRepository.deleteById(readStatus.getId());

    };
}
