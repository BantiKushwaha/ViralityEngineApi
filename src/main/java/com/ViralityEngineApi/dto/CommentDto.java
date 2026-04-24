package com.ViralityEngineApi.dto;

import com.ViralityEngineApi.entities.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long postId;
    private Long authorId;
    private String authorType;
    private String content;
    private Integer depthLevel;
}
