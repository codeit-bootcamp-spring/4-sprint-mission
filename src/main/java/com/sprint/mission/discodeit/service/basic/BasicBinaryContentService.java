package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.FileAccessException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponseDto create(BinaryContentCreateDto binaryContentCreateDto) {
        BinaryContent createContent = null;
        try {
            createContent = binaryContentMapper.toEntity(binaryContentCreateDto);
        } catch (IOException e) {
            // 트랜잭션시 롤백을 고려해서 RuntimeException을 상속받은 FileAccessException 형태로 예외 전환해서 던지도록 설정했습니다.
            throw new FileAccessException(ErrorCode.FILE_IO_ERROR);
        }
        binaryContentRepository.save(createContent);
        return binaryContentMapper.toDto(createContent);
    }

    @Override
    public BinaryContentResponseDto find(UUID id) {
        BinaryContent binaryContent = requireBinaryContent(id);
        return binaryContentMapper.toDto(binaryContent);
    }

    @Override
    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> idList) {
        List<BinaryContent> contents = requireBinaryContents(idList);

        return binaryContentMapper.toDtos(contents);
    }

    @Override
    public void delete(UUID id) {
        validateBinaryContentExists(id);
        binaryContentRepository.deleteById(id);
    }

    /* =========================================================
     * INTERNAL VALIDATION HELPERS
     * ========================================================= */

    /**
     * 주어진 ID에 해당하는 BinaryContent를 조회합니다.
     * 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param id 조회할 BinaryContent의 ID
     * @return 조회된 BinaryContent 엔티티
     * @throws NoSuchElementException 해당 ID의 BinaryContent가 존재하지 않는 경우
     */
    private BinaryContent requireBinaryContent(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Binary Content with id " + id + " not found"));
    }

    /**
     * 주어진 ID 목록에 해당하는 BinaryContent들을 모두 조회합니다.
     * 일부 ID가 누락된 경우 예외를 발생시킵니다.
     *
     * @param idList 조회할 BinaryContent ID 목록
     * @return 조회된 BinaryContent 리스트
     * @throws NoSuchElementException 일부 ID에 해당하는 BinaryContent가 존재하지 않는 경우
     */
    private List<BinaryContent> requireBinaryContents(List<UUID> idList) {
        if (idList == null || idList.isEmpty()) {
            return Collections.emptyList();
        }
        List<BinaryContent> found = binaryContentRepository.findAllByIdIn(idList);
        // 누락된 id 체크
        Set<UUID> foundIds = found.stream()
                .map(BinaryContent::getId)
                .collect(Collectors.toSet());
        List<UUID> missing = idList.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
        if (!missing.isEmpty()) {
            throw new NoSuchElementException("Binary Content not found for ids " + missing);
        }
        return found;
    }

    /**
     * 주어진 ID에 해당하는 BinaryContent가 존재하는지 검증합니다.
     * 삭제 작업 전 유효성 검사에 사용됩니다.
     *
     * @param id 검사할 BinaryContent의 ID
     * @throws NoSuchElementException 해당 ID의 BinaryContent가 존재하지 않는 경우
     */
    private void validateBinaryContentExists(UUID id) {
        if (!binaryContentRepository.existsById(id)) {
            throw new NoSuchElementException("Binary Content with id " + id + " not found");
        }
    }
}
