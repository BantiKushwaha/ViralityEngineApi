package com.ViralityEngineApi.repos;

import com.ViralityEngineApi.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepo extends JpaRepository<Post, Long> {
}
