package com.ViralityEngineApi.repos;

import com.ViralityEngineApi.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepo extends JpaRepository<Like, Long> {
    
    Optional<Like> findByPostIdAndUserIdAndUserType(Long postId, Long userId, String userType);
    
    boolean existsByPostIdAndUserIdAndUserType(Long postId, Long userId, String userType);
}
