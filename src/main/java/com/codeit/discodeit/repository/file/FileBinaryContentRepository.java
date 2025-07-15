package com.codeit.discodeit.repository.file;

import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileBinaryContentRepository implements BinaryContentRepository, Serializable {

    private final String filePath;

    public FileBinaryContentRepository(String fileDirectory) {
        this.filePath = fileDirectory + "/binary.ser";
    }

    @Override
    public List<BinaryContent> loadBinaryContents() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveBinaryContents(List<BinaryContent> contentsList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(contentsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                .filter(content -> content.getId().equals(binaryContentsId))
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
