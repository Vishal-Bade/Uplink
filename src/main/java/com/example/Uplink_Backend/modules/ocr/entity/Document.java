package com.example.Uplink_Backend.modules.ocr.entity;

import com.example.Uplink_Backend.modules.auth.entity.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(nullable = false, length = 50)
    private String type; // RESUME, CERTIFICATE, NOTICE, IDENTITY

    @Column(length = 50)
    private String status = "UPLOADED"; // UPLOADED, VERIFIED, REJECTED

    @Column(name = "uploaded_at", updatable = false)
    private Instant uploadedAt = Instant.now();

    public Document() {}

    public Document(User user, String name, String fileUrl, String type) {
        this.user = user;
        this.name = name;
        this.fileUrl = fileUrl;
        this.type = type;
        this.status = "UPLOADED";
        this.uploadedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
