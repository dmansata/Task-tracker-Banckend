package com.tasktracker.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectRequest {
    @NotBlank
    private String name;
    private String description;
}
