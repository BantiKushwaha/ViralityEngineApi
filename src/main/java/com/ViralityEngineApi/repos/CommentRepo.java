package com.ViralityEngineApi.repos;

import com.ViralityEngineApi.entities.Comment;
import com.ViralityEngineApi.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
