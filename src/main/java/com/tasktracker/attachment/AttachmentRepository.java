package com.tasktracker.attachment;

import com.tasktracker.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTaskOrderByCreatedAtDesc(Task task);
}
