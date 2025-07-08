package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.CreateUserDto;
import com.sprint.mission.discodeit.dto.data.UpdateUserDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Mapper.UpdateUserMapper;
import com.sprint.mission.discodeit.entity.Mapper.UserMapper;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JCFUserService implements UserService {

    private final Map<UUID, User> userList;
    UserMapper userMapper;
    UserStatus userStatus;

    public JCFUserService() {
        userList = new HashMap<>();
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userList.containsValue(user.getEmail()) && userList.containsValue(user.getNickName())) {
            throw new IllegalArgumentException("중복됩니다.");
        }
        userStatus = new UserStatus(user.getId(), userStatus.getLastActivatedAt());
        return userMapper.userCreateDtoToUser(user);

    }

    //    public User createUser(String nickName, String password) { // uid는 User에서 만듬
    //        User newUser = new User(nickName, password);
    //        userList.put(newUser.getId(), newUser);
    //        return newUser;
    //    }

    @Override
    public UserDto searchUser(UUID id) {
        User findUser = null;
        if(userList.containsKey(id) && userStatus.nowLogin()) {
            findUser = userList.get(id);
        }
        assert findUser != null; // null처리 하려고 했는데 얘가 자동완성됨
        return userMapper.userCreateDtoToUser(findUser);
    }
//    public User searchUser(UUID id) {
//        User findUser = null;
//        if (userList.containsKey(id)) {
//            findUser = userList.get(id);
//        }
//        return findUser;
//    }

    // bean
    // 메서드의 반환타입이 빈으로 등록됨
//    public List<User> searchAll() {
//        return this.userList.values().stream().toList();
//    }
    @Override
    public List<UserDto> searchAll() {
        List<CreateUserDto> userDTOList = new ArrayList<>();
        for(User user : userList.values()) {
            if(userStatus.nowLogin()) {
                userDTOList.add(userMapper.userCreateDtoToUser(userList.get(user)));
            }
        }
        return userDTOList;
    }

    @Override
    public UserDto updateUser(UUID id, String newNickName) {
        User updateUser = this.userList.get(id);
        UpdateUserMapper updateUserMapper = null;
        if(newNickName != null && !newNickName.equals(updateUser.getNickName())) {
           updateUser.setNickName(newNickName);
        } else {
            throw new NullPointerException();
        }

        return updateUserMapper.updateUserDTO(updateUser);
    }
//    public User updateUser(UUID id, String newNickName) {
//        User updatedUser = this.userList.get(id);
//        if(newNickName != null && !newNickName.equals(updatedUser.getNickName())) {
//            updatedUser.setNickName(newNickName);
//        }
//        return updatedUser;
//    }

    @Override
    public void deleteUser(UUID id) {
        if(!userList.containsKey(id)) {
            throw new NoSuchElementException("너는 계정을 삭제할 수 없다!");
        } else {
            userList.remove(id);
            // UserStatus, BinaryContent도 전부 삭제
        }
    }
}
