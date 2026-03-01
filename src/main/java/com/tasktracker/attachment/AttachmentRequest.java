package com.tasktracker.attachment;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentRequest {
    @NotBlank
    private String fileName;

    @NotBlank
    private String fileUrl;

    private String fileType;
    private Long fileSize;
}
