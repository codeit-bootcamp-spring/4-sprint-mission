package com.sprint.mission.discodeit.service.file;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileIOHelper {

    // 디렉토리 생성
    public static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + directory, e);
            }
        }
    }

    // Map 전체를 하나의 파일에 저장
    public static <K, V> void saveMap(Path filePath, Map<K, V> data) {
        try (
                FileOutputStream fos = new FileOutputStream(filePath.toFile());//해당 디렉터리에 있는 파일에 바이트 데이터를 쓸 수 있는 스트림 생성
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);//객체를 직렬화하여 바이트로 변경 후 fos를 통해 파일에 저장.
        } catch (IOException e) {
            throw new RuntimeException("데이터 저장 실패: " + filePath, e);
        }
    }

    // Map 전체를 하나의 파일에서 로드
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> loadMap(Path filePath) {
        if (Files.exists(filePath)) {
            try (
                    FileInputStream fis = new FileInputStream(filePath.toFile());//파일에서 바이트 데이터를 읽어옴
                    ObjectInputStream ois = new ObjectInputStream(fis)//바이트 데이터를 객체로 변환
            ) {
                return (Map<K, V>) ois.readObject();//직렬화된 객체를 역직렬화하여 반환
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("데이터 로딩 실패: " + filePath, e);
            }
        } else {
            return new HashMap<>();
        }
    }
}
