package com.ViralityEngineApi.services;

import com.ViralityEngineApi.dto.UserDto;
import com.ViralityEngineApi.entities.User;
import com.ViralityEngineApi.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepo repo;

    public User createUser(UserDto userDto){
        User user = User.builder()
                .userName(userDto.getUserName())
                .isPremium(userDto.getIsPremium())
                .build();

        return repo.save(user);
    }

    public User getUser(Long id){
        return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
