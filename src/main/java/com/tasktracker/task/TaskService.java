package com.tasktracker.task;

import com.tasktracker.attachment.Attachment;
import com.tasktracker.attachment.AttachmentRepository;
import com.tasktracker.attachment.AttachmentRequest;
import com.tasktracker.attachment.AttachmentResponse;
import com.tasktracker.comment.CommentRequest;
import com.tasktracker.comment.CommentResponse;
import com.tasktracker.comment.TaskComment;
import com.tasktracker.comment.TaskCommentRepository;
import com.tasktracker.exception.ForbiddenException;
import com.tasktracker.exception.ResourceNotFoundException;
import com.tasktracker.project.Project;
import com.tasktracker.project.ProjectService;
import com.tasktracker.user.User;
import com.tasktracker.user.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final TaskCommentRepository taskCommentRepository;
    private final AttachmentRepository attachmentRepository;

    public TaskResponse createTask(CreateTaskRequest request, User currentUser) {
        Project project = projectService.getProjectForMember(request.getProjectId(), currentUser);
        User assignee = resolveAssignee(request.getAssignedToUserId(), currentUser, project);

        Task task = new Task();
        task.setProject(project);
        task.setCreatedBy(currentUser);
        task.setAssignedTo(assignee);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.OPEN);

        return toTaskResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getMyTasks(User currentUser, TaskStatus status, String search, String sortBy, String sortDir) {
        Sort sort = buildSort(sortBy, sortDir);
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("assignedTo").get("id"), currentUser.getId()));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleLike, descLike));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return taskRepository.findAll(spec, sort).stream().map(this::toTaskResponse).toList();
    }

    public TaskResponse getTaskById(Long taskId, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        return toTaskResponse(task);
    }

    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getAssignedToUserId() != null) {
            User assignee = resolveAssignee(request.getAssignedToUserId(), currentUser, task.getProject());
            task.setAssignedTo(assignee);
        }

        return toTaskResponse(taskRepository.save(task));
    }

    public void deleteTask(Long taskId, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        boolean canDelete = task.getCreatedBy().getId().equals(currentUser.getId()) ||
                task.getProject().getOwner().getId().equals(currentUser.getId());
        if (!canDelete) {
            throw new ForbiddenException("Only task creator or project owner can delete this task");
        }
        taskRepository.delete(task);
    }

    public TaskResponse assignTask(Long taskId, Long assigneeId, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        User assignee = resolveAssignee(assigneeId, currentUser, task.getProject());
        task.setAssignedTo(assignee);
        return toTaskResponse(taskRepository.save(task));
    }

    public TaskResponse updateStatus(Long taskId, TaskStatus status, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        task.setStatus(status);
        return toTaskResponse(taskRepository.save(task));
    }

    public CommentResponse addComment(Long taskId, CommentRequest request, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        TaskComment comment = new TaskComment();
        comment.setTask(task);
        comment.setAuthor(currentUser);
        comment.setContent(request.getContent());
        TaskComment saved = taskCommentRepository.save(comment);
        return toCommentResponse(saved);
    }

    public List<CommentResponse> getComments(Long taskId, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        return taskCommentRepository.findByTaskOrderByCreatedAtAsc(task)
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    public AttachmentResponse addAttachment(Long taskId, AttachmentRequest request, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setUploadedBy(currentUser);
        attachment.setFileName(request.getFileName());
        attachment.setFileUrl(request.getFileUrl());
        attachment.setFileType(request.getFileType());
        attachment.setFileSize(request.getFileSize());
        Attachment saved = attachmentRepository.save(attachment);
        return toAttachmentResponse(saved);
    }

    public List<AttachmentResponse> getAttachments(Long taskId, User currentUser) {
        Task task = getTaskForProjectMember(taskId, currentUser);
        return attachmentRepository.findByTaskOrderByCreatedAtDesc(task)
                .stream()
                .map(this::toAttachmentResponse)
                .toList();
    }

    private Task getTaskForProjectMember(Long taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        Set<Long> memberIds = task.getProject().getMembers().stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
        if (!memberIds.contains(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to access this task");
        }
        return task;
    }

    private User resolveAssignee(Long assigneeId, User fallbackUser, Project project) {
        User assignee = assigneeId == null
                ? fallbackUser
                : userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee user not found: " + assigneeId));
        boolean isMember = project.getMembers().stream().anyMatch(u -> u.getId().equals(assignee.getId()));
        if (!isMember) {
            throw new ForbiddenException("Assignee must be a member of the task project");
        }
        return assignee;
    }

    private Sort buildSort(String sortBy, String sortDir) {
        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "dueDate" : sortBy;
        List<String> allowed = List.of("dueDate", "createdAt", "updatedAt", "title", "status");
        if (!allowed.contains(safeSortBy)) {
            safeSortBy = "dueDate";
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, safeSortBy);
    }

    private TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getProject().getId(),
                task.getCreatedBy().getId(),
                task.getAssignedTo().getId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private CommentResponse toCommentResponse(TaskComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTask().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getEmail(),
                comment.getCreatedAt()
        );
    }

    private AttachmentResponse toAttachmentResponse(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getTask().getId(),
                attachment.getUploadedBy().getId(),
                attachment.getCreatedAt()
        );
    }
}
