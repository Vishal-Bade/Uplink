package com.example.Uplink_Backend.common.controller;

import com.example.Uplink_Backend.common.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Cross-Cutting: File Storage", description = "Endpoints for downloading and viewing stored files (Supabase Storage / Local)")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/download/**")
    @Operation(summary = "Download or stream stored file by relative path")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        String relativePath = fullPath.substring(fullPath.indexOf("/download/") + "/download/".length());

        byte[] data = fileStorageService.loadFileAsBytes(relativePath);
        if (data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String filename = relativePath.contains("/") ? relativePath.substring(relativePath.lastIndexOf('/') + 1) : relativePath;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
