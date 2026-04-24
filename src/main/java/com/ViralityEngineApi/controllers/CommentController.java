package com.ViralityEngineApi.controllers;

import com.ViralityEngineApi.dto.CommentDto;
import com.ViralityEngineApi.entities.Comment;
import com.ViralityEngineApi.entities.Post;
import com.ViralityEngineApi.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    CommentService service;

    @PostMapping
    public Comment createComment(@RequestBody CommentDto commentDto){
        return service.addComment(commentDto);
    }

    @GetMapping
    public List<Comment> getComments(@RequestParam Long postId){
        return service.getComments(postId);
    }
}
