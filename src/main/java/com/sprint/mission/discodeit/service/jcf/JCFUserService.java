package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final HashMap<UUID , User> data = new HashMap<>();

    @Override
    public User createUser(User user){
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID id){
        return data.get(id);
    }

    @Override
    public List<User> getUsers(){
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateUser(UUID id, String username){
        User user = data.get(id);
        if(user != null){
            user.updateName(username);
        }
    }

    @Override
    public boolean deleteUser(UUID id) {
        if(!data.containsKey(id)){
            System.out.println("삭제할 유저가 존재하지 않습니다.");
            return false;
        }
        data.remove(id);
        return true;
        }
    }

