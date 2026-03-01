package com.tasktracker.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private Long taskId;
    private Long authorId;
    private String authorEmail;
    private LocalDateTime createdAt;
}
