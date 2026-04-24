package com.ViralityEngineApi.controllers;

import com.ViralityEngineApi.dto.BotDto;
import com.ViralityEngineApi.entities.Bot;
import com.ViralityEngineApi.services.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot")
public class BotController {

    @Autowired
    BotService service;

    @PostMapping
    public Bot createBot(@RequestBody BotDto botDto){
        return service.createBot(botDto);
    }

    @GetMapping
    public Bot getBot(@RequestParam Long id ){
        return service.getBot(id);
    }
}
