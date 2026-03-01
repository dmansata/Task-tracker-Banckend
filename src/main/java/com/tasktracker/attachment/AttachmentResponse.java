package com.tasktracker.attachment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long taskId;
    private Long uploadedByUserId;
    private LocalDateTime createdAt;
}
