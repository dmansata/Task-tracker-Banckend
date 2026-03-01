package com.tasktracker.task;

import com.tasktracker.attachment.AttachmentRequest;
import com.tasktracker.comment.CommentRequest;
import com.tasktracker.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.createTask(request, currentUser));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(taskService.getMyTasks(currentUser, status, search, sortBy, sortDir));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.getTaskById(taskId, currentUser));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, currentUser));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        taskService.deleteTask(taskId, currentUser);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }

    @PatchMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.assignTask(taskId, userId, currentUser));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, status, currentUser));
    }

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.addComment(taskId, request, currentUser));
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.getComments(taskId, currentUser));
    }

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<?> addAttachment(
            @PathVariable Long taskId,
            @Valid @RequestBody AttachmentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.addAttachment(taskId, request, currentUser));
    }

    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<?> getAttachments(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(taskService.getAttachments(taskId, currentUser));
    }
}
