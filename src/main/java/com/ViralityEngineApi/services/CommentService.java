package com.ViralityEngineApi.services;

import com.ViralityEngineApi.dto.CommentDto;
import com.ViralityEngineApi.entities.Comment;
import com.ViralityEngineApi.entities.Post;
import com.ViralityEngineApi.repos.CommentRepo;
import com.ViralityEngineApi.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepo commentRepo;

    @Autowired
    PostRepo postRepo;

    @Autowired
    ViralityEngineService viralityEngineService;

    public Comment addComment(CommentDto commentDto){
        Post post = postRepo.findById(commentDto.getPostId()).orElseThrow(()-> new RuntimeException("Post not found"));

        if (!viralityEngineService.checkCommentDepth(commentDto.getDepthLevel())) {
            throw new RuntimeException("400 Bad Request: Comment thread depth exceeded");
        }

        if ("bot".equalsIgnoreCase(commentDto.getAuthorType())) {
            if (!viralityEngineService.checkAndIncrementBotCount(commentDto.getPostId())) {
                throw new RuntimeException("429 Too Many Requests: Maximum bot replies per post exceeded");
            }
            
            if (!viralityEngineService.checkAndSetBotHumanCooldown(commentDto.getAuthorId(), post.getAuthorId())) {
                throw new RuntimeException("429 Too Many Requests: Bot-human cooldown active");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .authorId(commentDto.getAuthorId())
                .authorType(commentDto.getAuthorType())
                .content(commentDto.getContent())
                .depthLevel(commentDto.getDepthLevel())
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepo.save(comment);
        
        if ("bot".equalsIgnoreCase(commentDto.getAuthorType())) {
            viralityEngineService.incrementViralityScore(commentDto.getPostId(), "bot_reply");
        } else if ("human".equalsIgnoreCase(commentDto.getAuthorType())) {
            viralityEngineService.incrementViralityScore(commentDto.getPostId(), "human_comment");
        }
        
        return savedComment;
    }

    public List<Comment> getComments(Long postId){
        Post post = postRepo.findById(postId).orElseThrow(()-> new RuntimeException("Post not found"));
        return commentRepo.findByPost(post);
    }

}
