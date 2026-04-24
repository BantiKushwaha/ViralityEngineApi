package com.ViralityEngineApi.controllers;

import com.ViralityEngineApi.dto.LikeDto;
import com.ViralityEngineApi.dto.PostDto;
import com.ViralityEngineApi.entities.Like;
import com.ViralityEngineApi.entities.Post;
import com.ViralityEngineApi.services.LikeService;
import com.ViralityEngineApi.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    PostService service;

    @Autowired
    LikeService likeService;

    @PostMapping
    public Post createPost(@RequestBody PostDto postDto){
        return service.createPost(postDto);
    }

    @GetMapping
    public Post getPost(@RequestParam Long postId){
        return service.getPost(postId);
    }

    @PostMapping("/{postId}/like")
    public Like likePost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeDto.setPostId(postId);
        return likeService.createLike(likeDto);
    }

}
