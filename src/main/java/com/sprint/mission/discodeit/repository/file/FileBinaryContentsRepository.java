package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContents;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileBinaryContentsRepository implements BinaryContentsRepository, Serializable {

    private final String filePath;

    public FileBinaryContentsRepository(String fileDirectory) {
        this.filePath = fileDirectory + "/binary.ser";
    }

    @Override
    public List<BinaryContents> loadBinaryContents() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // 비어 있으면 빈 리스트 반환
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<BinaryContents>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveBinaryContents(List<BinaryContents> contentsList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(contentsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
