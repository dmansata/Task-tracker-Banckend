package com.tasktracker.comment;

import com.tasktracker.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTaskOrderByCreatedAtAsc(Task task);
}
