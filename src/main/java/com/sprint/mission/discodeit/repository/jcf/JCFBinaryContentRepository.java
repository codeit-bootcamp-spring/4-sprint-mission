package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final List<BinaryContent> data = new ArrayList<>();

    @Override
    public void delete(UUID id) {
        data.removeIf(bc -> bc.getId().equals(id));
    }

    @Override
    public void save(BinaryContent binaryContent) {
        if (data.contains(binaryContent)) {
            data.stream()
                    .filter(bc -> bc.equals(binaryContent))
                    .forEach(bc -> {
                        delete(bc.getId());
                    });
        }
        data.add(binaryContent);
    }

    @Override
    public BinaryContent findById(UUID id) {
        BinaryContent binaryContent = data.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst().orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARYCONTENT_NOT_FOUND));
        return binaryContent;
    }

    @Override
    public List<BinaryContent> findAllById(List<UUID> ids) {
        List<BinaryContent> binaryContents = new ArrayList<>();
        ids.stream()
                .forEach(id -> {
                    BinaryContent binaryContent = data.stream()
                            .filter(biContent -> biContent.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARYCONTENT_NOT_FOUND));
                    binaryContents.add(binaryContent);
                });
        return binaryContents;
    }

    //
    @Override
    public List<BinaryContent> findAll() {
        return data;
    }
}
