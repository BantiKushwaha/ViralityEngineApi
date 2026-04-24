package com.ViralityEngineApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long authorId;
    private String authorType;
    private String content;

}
