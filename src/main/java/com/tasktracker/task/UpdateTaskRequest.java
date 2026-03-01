package com.tasktracker.task;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTaskRequest {
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long assignedToUserId;
}
