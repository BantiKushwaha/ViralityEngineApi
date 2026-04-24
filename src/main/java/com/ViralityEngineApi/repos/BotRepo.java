package com.ViralityEngineApi.repos;

import com.ViralityEngineApi.entities.Bot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepo extends JpaRepository<Bot, Long> {
}
