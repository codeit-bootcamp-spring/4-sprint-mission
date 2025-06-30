package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaApplication {

    public static void main(String[] args) {

//        // IO,직렬화 테스트
//
//        Path userDirectory = Paths.get(System.getProperty("user.dir"), "data");
//
//        init(userDirectory);
//
//        List.of(
//                new User("레드","qwe123"),
//                new User("블루", "asd123"),
//                new User("그린", "zxc123")
//        ).forEach(user -> {
//
//            Path filePath = userDirectory.resolve(user.getNickName().concat(".ser")); // Path의 P는 대문자이다
//            save(filePath, user);
//
//        });
//
//        load(userDirectory)
//                .forEach(data -> System.out.println(data));

    }
}
