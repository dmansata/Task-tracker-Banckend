package com.tasktracker.attachment;

import com.tasktracker.task.Task;
import com.tasktracker.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 2048)
    private String fileUrl;

    private String fileType;

    private Long fileSize;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
