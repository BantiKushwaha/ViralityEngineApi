package com.ViralityEngineApi.services;

import com.ViralityEngineApi.dto.BotDto;
import com.ViralityEngineApi.entities.Bot;
import com.ViralityEngineApi.repos.BotRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotService {

    @Autowired
    BotRepo repo;

    public Bot createBot(BotDto botDto){
        Bot bot = Bot.builder()
                .name(botDto.getName())
                .personDesc(botDto.getPersonDesc())
                .build();
        return repo.save(bot);
    }

    public Bot getBot(Long id){
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Bot not found"));
    }
}
