package com.tasktracker.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerEmail;
    private int memberCount;
}
