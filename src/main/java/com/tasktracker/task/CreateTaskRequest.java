package com.tasktracker.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTaskRequest {
    @NotNull
    private Long projectId;

    @NotBlank
    private String title;

    private String description;

    private LocalDate dueDate;

    private Long assignedToUserId;
}
