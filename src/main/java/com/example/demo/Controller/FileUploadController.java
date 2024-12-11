package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.exception.FileStorageException;
import com.example.demo.service.FileStorageService;

@RestController
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload/film-cover")
    public ResponseEntity<?> uploadFilmCover(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            if (!fileStorageService.isValidFileType(file, "image/jpeg", "image/png")) {
                return ResponseEntity.badRequest()
                    .body("Hanya file JPEG dan PNG yang diperbolehkan");
            }

            // Validate file size (max 5MB)
            if (!fileStorageService.isValidFileSize(file, 5)) {
                return ResponseEntity.badRequest()
                    .body("Ukuran file maksimal 5MB");
            }

            String fileName = fileStorageService.storeFile(file, "covers");
            
            return ResponseEntity.ok(Map.of(
                "fileName", fileName,
                "message", "File berhasil diupload"
            ));

        } catch (FileStorageException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Gagal mengupload file: " + ex.getMessage());
        }
    }
}
