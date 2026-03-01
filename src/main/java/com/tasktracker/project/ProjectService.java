package com.tasktracker.project;

import com.tasktracker.exception.ForbiddenException;
import com.tasktracker.exception.ResourceNotFoundException;
import com.tasktracker.user.User;
import com.tasktracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(CreateProjectRequest request, User currentUser) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(currentUser);
        project.getMembers().add(currentUser);
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    public List<ProjectResponse> getMyProjects(User currentUser) {
        return projectRepository.findByMembersContaining(currentUser).stream().map(this::toResponse).toList();
    }

    public ProjectResponse addMember(Long projectId, AddMemberRequest request, User currentUser) {
        Project project = getProject(projectId);
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only project owner can add members");
        }
        User newMember = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        project.getMembers().add(newMember);
        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    public Project getProjectForMember(Long projectId, User currentUser) {
        Project project = getProject(projectId);
        boolean isMember = project.getMembers().stream().anyMatch(u -> u.getId().equals(currentUser.getId()));
        if (!isMember) {
            throw new ForbiddenException("You are not a member of this project");
        }
        return project;
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getEmail(),
                project.getMembers().size()
        );
    }
}
