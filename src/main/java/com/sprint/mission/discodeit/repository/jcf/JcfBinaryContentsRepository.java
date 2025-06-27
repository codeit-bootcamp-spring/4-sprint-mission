package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JcfBinaryContentsRepository implements BinaryContentsRepository {

    private List<BinaryContents> binaryContentsData = new ArrayList<>();

    @Override
    public List<BinaryContents> loadBinaryContents() {
        return binaryContentsData;
    }

    @Override
    public void saveBinaryContents(List<BinaryContents> contentsList) {
        binaryContentsData = contentsList;
    }

    @Override
    public void createBinaryContents(BinaryContents contents) {
        List<BinaryContents> contentsList = loadBinaryContents();
        contentsList.add(contents);
        saveBinaryContents(contentsList);
    }

    @Override
    public Optional<BinaryContents> findBinaryContentsByBinaryContentsId(UUID binaryContentsId){
        List<BinaryContents> contentsList = loadBinaryContents();
        return contentsList.stream()
                .filter(content -> content.equalsId(binaryContentsId))
                .findFirst(); // 없으면 null 반환
    }

    @Override
    public void deleteBinaryContensByBinaryContentsId(UUID binaryContentId){
        List<BinaryContents> contentsList = loadBinaryContents();
        contentsList.removeIf(content -> content.equalsId(binaryContentId));
        saveBinaryContents(contentsList);
    }

    @Override
    public List<BinaryContents> findBinaryContentsByReferenceId(UUID referenceId){
        List<BinaryContents> contentsList = loadBinaryContents();
        return contentsList.stream().filter(content -> content.getReferenceId().equals(referenceId)).toList();
    }

}
