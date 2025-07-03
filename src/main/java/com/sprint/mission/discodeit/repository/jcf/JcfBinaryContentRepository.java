package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JcfBinaryContentRepository implements BinaryContentRepository {

    private List<BinaryContent> binaryContentData = new ArrayList<>();

    @Override
    public List<BinaryContent> loadBinaryContents() {
        return binaryContentData;
    }

    @Override
    public void saveBinaryContents(List<BinaryContent> contentsList) {
        binaryContentData = contentsList;
    }

    @Override
    public void createBinaryContent(BinaryContent contents) {
        List<BinaryContent> contentsList = loadBinaryContents();
        contentsList.add(contents);
        saveBinaryContents(contentsList);
    }

    @Override
    public Optional<BinaryContent> findBinaryContentByBinaryContentId(UUID binaryContentsId){
        List<BinaryContent> contentsList = loadBinaryContents();
        return contentsList.stream()
                .filter(content -> content.equalsId(binaryContentsId))
                .findFirst(); // 없으면 null 반환
    }

    @Override
    public void deleteBinaryContentByBinaryContentId(UUID binaryContentId){
        List<BinaryContent> contentsList = loadBinaryContents();
        contentsList.removeIf(content -> content.equalsId(binaryContentId));
        saveBinaryContents(contentsList);
    }

    @Override
    public List<BinaryContent> findBinaryContentListByReferenceId(UUID referenceId){
        List<BinaryContent> contentsList = loadBinaryContents();
        return contentsList.stream().filter(content -> content.getReferenceId().equals(referenceId)).toList();
    }

}
