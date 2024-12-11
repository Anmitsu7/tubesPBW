package com.example.demo.service;

import com.example.demo.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Gagal membuat direktori untuk menyimpan file.", ex);
        }
    }

    public String storeFile(MultipartFile file, String subdirectory) {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Gagal menyimpan file kosong.");
            }

            // Buat subdirectory jika belum ada
            Path targetLocation = this.fileStorageLocation.resolve(subdirectory);
            if (!Files.exists(targetLocation)) {
                Files.createDirectories(targetLocation);
            }

            // Normalize file name
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            
            // Check invalid characters
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Maaf! Nama file mengandung path yang tidak valid " + originalFileName);
            }

            // Generate unique filename
            String fileExtension = getFileExtension(originalFileName);
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to target location
            Path targetPath = targetLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return subdirectory + "/" + newFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Gagal menyimpan file.", ex);
        }
    }

    public void deleteFile(String filePath) {
        try {
            Path targetPath = this.fileStorageLocation.resolve(filePath);
            Files.deleteIfExists(targetPath);
        } catch (IOException ex) {
            throw new FileStorageException("Gagal menghapus file.", ex);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public boolean isValidFileType(MultipartFile file, String... allowedTypes) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        for (String type : allowedTypes) {
            if (contentType.startsWith(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidFileSize(MultipartFile file, long maxSizeInMB) {
        return (file.getSize() / 1_048_576) <= maxSizeInMB;
    }
}