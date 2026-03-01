package com.tasktracker.project;

import com.tasktracker.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByMembersContaining(User member);
}
