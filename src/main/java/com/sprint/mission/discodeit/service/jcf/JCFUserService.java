package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    /**싱글톤 패턴을 적용하여 JCFUserService의 단일 인스턴스만 생성되도록 합니다*/
   private static final JCFUserService instance = new JCFUserService();
   private JCFUserService() {}
    public static JCFUserService getInstance() {
       return instance;
    }

    private final Map<String, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        users.put(user.getUserId().toString(), user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * 유저 이름을 변경합니다.
     *
     * @param updatedText 유저가 입력한 새 유저명
     */
    @Override
    public void updateUser(UUID userId, String updatedText) {
        Optional.ofNullable(users.get(userId.toString()))
                .ifPresent(user -> {
                    user.setUserName(updatedText);
                    user.setUpdatedAt(System.currentTimeMillis());
                });
        }


    @Override
    public void deleteUser(UUID userId) {
        users.remove(userId.toString());
    }

    @Override
    public User getUserById(UUID userId) {
        return users.get(userId.toString());
    }

    /**
     * 입력된 키워드로 유저를 조회합니다.
     *
     * @param keyword 입력된 키워드
     */
    @Override
    public void findUsersByKeyword(String keyword) {
    System.out.println("[" + keyword + "] 키워드로 검색한 결과: ");
        for (User user : users.values()) {
            if(user.getUserName().toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println(user);
            }
        }
    }

    /**
     * 변경된 유저 상태를 반영합니다.
     *
     * @param status 유저 상태
     */
    @Override
    public void updateUserStatus (UUID userId, UserStatus status) {
        Optional.ofNullable(users.get(userId.toString()))
                .ifPresent(user -> {
                    user.setUserStatus(status);
                    user.setUpdatedAt(System.currentTimeMillis());
                });

    }

    /**
     * 해당 상태에 있는 모든 유저를 반환합니다.
     *
     * @param status 유저 상태
     * @return 해당 상태에있는 유저 목록
     */
    @Override
    public List<User> findUsersByStatus(UserStatus status) {
        List<User> result = new ArrayList<>();
            for (User user : users.values()) {
                // 상태가 일치하는 유저만 결과에 추가
                if(user.getUserStatus().equals(status)){
                    result.add(user);
                }
            }
            return result;
    }

}
