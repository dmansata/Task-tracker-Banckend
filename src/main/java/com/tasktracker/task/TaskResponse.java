package com.tasktracker.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long projectId;
    private Long createdByUserId;
    private Long assignedToUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
