package com.example.Uplink_Backend.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation;

    @Value("${uplink.storage.supabase.url:}")
    private String supabaseUrl;

    public FileStorageService(@Value("${uplink.storage.upload-dir:./uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            logger.error("Could not create upload directory: {}", uploadDir, ex);
        }
    }

    public String storeFile(MultipartFile file, String subfolder) {
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String fileExtension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFilename.substring(i);
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetFolder = this.fileStorageLocation.resolve(subfolder != null ? subfolder : "general");
            Files.createDirectories(targetFolder);

            Path targetLocation = targetFolder.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // If Supabase Storage is configured in env, we can format standard Supabase URL; otherwise local file endpoint
            if (supabaseUrl != null && !supabaseUrl.isBlank()) {
                return String.format("%s/storage/v1/object/public/uplink-files/%s/%s", supabaseUrl, subfolder, fileName);
            }

            return "/api/files/download/" + (subfolder != null ? subfolder + "/" : "") + fileName;
        } catch (IOException ex) {
            logger.error("Failed to store file: {}", fileName, ex);
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public byte[] loadFileAsBytes(String relativePath) {
        try {
            Path filePath = this.fileStorageLocation.resolve(relativePath).normalize();
            File file = filePath.toFile();
            if (file.exists()) {
                return Files.readAllBytes(filePath);
            }
            return new byte[0];
        } catch (IOException e) {
            logger.error("Error reading file: {}", relativePath, e);
            return new byte[0];
        }
    }
}
