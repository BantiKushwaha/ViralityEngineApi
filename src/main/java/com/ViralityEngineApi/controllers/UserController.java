package com.ViralityEngineApi.controllers;

import com.ViralityEngineApi.dto.UserDto;
import com.ViralityEngineApi.entities.User;
import com.ViralityEngineApi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService service;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto){
        System.out.println("In controller : "+ userDto.getUserName() + userDto.getIsPremium());
        return service.createUser(userDto);
    }

    @GetMapping
    public User getUser(@RequestParam Long id){
        return service.getUser(id);
    }
}
