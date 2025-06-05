package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    // 등록
    @Override
    public User createUser(String name) {
        User createUser = new User(name);
        data.add(createUser);
        return createUser;
    }

    // 단건 조회
//    @Override
//    public User findUserById(UUID id) {
//        for (User user : data) {
//            if (user.getId().equals(id)) {
//                return user;
//            }
//        }
//        return null;
//    }
    // Stream 변환
    @Override
    public User findUserById(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 다건 조회
    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data);
    }

    // 수정
/*    @Override
    public User updateUserById(UUID id, String newName) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                user.setUsername(newName);
                user.setUpdatedAt();
                return user;
            }
        }
        return null;
    }*/
    // Stream 변환
    /*
     * update 로직 : 찾음 > 수정 > 반환
     * filter > findFirst로 찾고, map으로 수정
     * 여기서 map은 중간 연산자 아니고, Optional!!
     *
     * +) Stream은 내부 반복자라 전체를 내부 순회하므로 update 할 요소가 많아지면 Stream 사용 권장 X
     */
    @Override
    public User updateUserById(UUID id, String newName) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(user -> {
                    user.setUsername(newName);
                    user.setUpdatedAt();
                    return user;
                })
                .orElse(null);
    }

    // 삭제
    /*
     * delete는 Stream 사용 시 side effect 발생하므로 변경 X
     */
    @Override
    public User deleteUserById(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                for (Channel channel : user.getChannels()) {
                    channel.getMembers().remove(user);
                }
                data.remove(user);
                return user;
            }
        }
        return null;
    }
}
