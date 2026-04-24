package com.ViralityEngineApi.services;

import com.ViralityEngineApi.dto.LikeDto;
import com.ViralityEngineApi.entities.Like;
import com.ViralityEngineApi.repos.LikeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LikeService {

    @Autowired
    private LikeRepo repo;

    @Autowired
    private ViralityEngineService viralityEngineService;

    public Like createLike(LikeDto likeDto) {
        if (repo.existsByPostIdAndUserIdAndUserType(
                likeDto.getPostId(), likeDto.getUserId(), likeDto.getUserType())) {
            throw new RuntimeException("User has already liked this post");
        }

        Like like = Like.builder()
                .postId(likeDto.getPostId())
                .userId(likeDto.getUserId())
                .userType(likeDto.getUserType())
                .createdAt(LocalDateTime.now())
                .build();

        Like savedLike = repo.save(like);
        
        if ("human".equalsIgnoreCase(likeDto.getUserType())) {
            viralityEngineService.incrementViralityScore(likeDto.getPostId(), "human_like");
        }
        
        return savedLike;
    }

    public void removeLike(Long postId, Long userId, String userType) {
        Like like = repo.findByPostIdAndUserIdAndUserType(postId, userId, userType)
                .orElseThrow(() -> new RuntimeException("Like not found"));
        repo.delete(like);
    }
}
