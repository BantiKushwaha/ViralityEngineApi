package com.ViralityEngineApi.repos;

import com.ViralityEngineApi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
