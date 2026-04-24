package com.ViralityEngineApi.services;

import com.ViralityEngineApi.dto.PostDto;
import com.ViralityEngineApi.entities.Post;
import com.ViralityEngineApi.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService {

    @Autowired
    PostRepo repo;

    public Post createPost(PostDto postDto){
        Post post = Post.builder()
                .authorId(postDto.getAuthorId())
                .authorType(postDto.getAuthorType())
                .content(postDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return repo.save(post);
    }

    public Post getPost(Long postId){
        return repo.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
    }
}
