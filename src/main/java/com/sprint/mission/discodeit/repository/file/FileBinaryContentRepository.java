package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    @Value("${discodeit.repository.file-directory}/BinaryContents.ser")
    private String FILE_PATH;

    @Override
    public List<BinaryContent> findAll() {
        List<BinaryContent> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            list = (List<BinaryContent>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<BinaryContent> findAllById(List<UUID> ids) {
        List<BinaryContent> list = new ArrayList<>();
        ids.stream()
                .forEach(id -> {
                    BinaryContent binaryContent = findAll().stream()
                            .filter(biContent -> biContent.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARYCONTENT_NOT_FOUND));
                    list.add(binaryContent);
                });
//        ids.stream()
//                .forEach(id -> {
//                    List<BinaryContent> binaryContentList = findAll().stream()
//                            .filter(content -> content.getId().equals(id))
//                            .toList();
//                    list.addAll(binaryContentList);
//                });
        return list;
    }

    public void saveAll(List<BinaryContent> binaryContents) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(binaryContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(UUID id) {
        List<BinaryContent> binaryContents = findAll();
        BinaryContent binaryContent = binaryContents.stream()
                .filter(biContent -> biContent.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARYCONTENT_NOT_FOUND));
        binaryContents.remove(binaryContent);
        saveAll(binaryContents);
    }

    @Override
    public void save(BinaryContent binaryContent) {
        List<BinaryContent> binaryContents = findAll();
        if (binaryContents.stream()
                .anyMatch(biContent -> biContent.equals(binaryContent)))
            throw new BusinessLogicException(ExceptionCode.BINARYCONTENT_EXISTS);
        binaryContents.add(binaryContent);
        saveAll(binaryContents);
    }

    @Override
    public BinaryContent findById(UUID id) {
        List<BinaryContent> binaryContents = findAll();
        return binaryContents.stream().filter(binaryContent -> binaryContent.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BINARYCONTENT_NOT_FOUND));
    }
}