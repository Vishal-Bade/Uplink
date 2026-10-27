package com.example.Uplink_Backend.modules.content.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePostRequest {
    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String type = "POST"; // POST, BLOG, NOTICE

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
