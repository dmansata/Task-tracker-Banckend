package com.tasktracker.project;

import com.tasktracker.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(projectService.createProject(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(projectService.getMyProjects(currentUser));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectResponse> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(projectService.addMember(projectId, request, currentUser));
    }
}
