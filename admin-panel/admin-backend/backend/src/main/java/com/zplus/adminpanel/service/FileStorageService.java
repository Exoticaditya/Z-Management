package com.zplus.adminpanel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file storage operations
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    private Path uploadPath;
    private final String baseUrl;
    
    // Allowed image types
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    
    // Max file size: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public FileStorageService(@Value("${app.upload-dir:./uploads}") String uploadDir,
                             @Value("${app.frontend-url:http://localhost:8080}") String frontendUrl) {
        
        // For Railway deployment, use a writable temporary directory
        String actualUploadDir = uploadDir;
        if (System.getenv("RAILWAY_ENVIRONMENT") != null || System.getenv("PORT") != null) {
            actualUploadDir = "/tmp/uploads";
            logger.info("Railway environment detected, using /tmp/uploads for file storage");
        }
        
        this.uploadPath = Paths.get(actualUploadDir).toAbsolutePath().normalize();
        this.baseUrl = frontendUrl + "/api/files/";
        
        try {
            Files.createDirectories(this.uploadPath);
            logger.info("Upload directory created at: {}", this.uploadPath);
            
            // Test write permissions
            Path testFile = this.uploadPath.resolve("test-write.tmp");
            Files.write(testFile, "test".getBytes());
            Files.deleteIfExists(testFile);
            logger.info("Upload directory is writable: {}", this.uploadPath);
            
        } catch (IOException ex) {
            logger.error("Could not create upload directory: {}", this.uploadPath, ex);
            
            // Fallback to system temp directory
            try {
                String tempDir = System.getProperty("java.io.tmpdir");
                this.uploadPath = Paths.get(tempDir, "zplus-uploads").toAbsolutePath().normalize();
                Files.createDirectories(this.uploadPath);
                logger.warn("Using fallback upload directory: {}", this.uploadPath);
            } catch (IOException fallbackEx) {
                logger.error("Could not create fallback upload directory", fallbackEx);
                throw new RuntimeException("Could not create upload directory!", fallbackEx);
            }
        }
    }

    /**
     * Store profile photo for a user
     */
    public String storeProfilePhoto(MultipartFile file, String userReference) {
        try {
            // Validate file
            validateFile(file);
            
            // Generate unique filename
            String fileName = generateProfilePhotoName(file, userReference);
            
            // Create user-specific directory
            Path userDir = this.uploadPath.resolve("profiles").resolve(userReference);
            Files.createDirectories(userDir);
            
            // Store file
            Path targetLocation = userDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            String relativePath = "profiles/" + userReference + "/" + fileName;
            logger.info("Profile photo stored successfully: {}", relativePath);
            
            return relativePath;
            
        } catch (IOException ex) {
            logger.error("Failed to store profile photo for user: {}", userReference, ex);
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    /**
     * Get full URL for a stored file
     */
    public String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        return baseUrl + filePath;
    }

    /**
     * Delete a file
     */
    public void deleteFile(String filePath) {
        try {
            if (filePath != null && !filePath.isEmpty()) {
                Path fileToDelete = this.uploadPath.resolve(filePath);
                Files.deleteIfExists(fileToDelete);
                logger.info("File deleted: {}", filePath);
            }
        } catch (IOException ex) {
            logger.error("Failed to delete file: {}", filePath, ex);
        }
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPG, PNG, and GIF images are allowed");
        }
        
        // Check filename
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (fileName.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence: " + fileName);
        }
    }

    /**
     * Generate unique filename for profile photo
     */
    private String generateProfilePhotoName(MultipartFile file, String userReference) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("profile_%s_%s_%s%s", userReference, timestamp, uniqueId, fileExtension);
    }

    /**
     * Get file path from URL
     */
    public Path getFilePath(String fileName) {
        return this.uploadPath.resolve(fileName).normalize();
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String filePath) {
        try {
            Path file = this.uploadPath.resolve(filePath);
            return Files.exists(file);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Get file size
     */
    public long getFileSize(String filePath) {
        try {
            Path file = this.uploadPath.resolve(filePath);
            return Files.size(file);
        } catch (IOException ex) {
            return 0;
        }
    }

    /**
     * Create thumbnail for profile photo (placeholder for future implementation)
     */
    public String createThumbnail(String originalPath) {
        // TODO: Implement thumbnail generation using image processing library
        // For now, return the original path
        return originalPath;
    }
}
