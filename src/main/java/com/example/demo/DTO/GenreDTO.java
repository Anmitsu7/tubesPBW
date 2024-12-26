package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenreDTO {
    private Long id;

    @NotBlank(message = "Nama genre tidak boleh kosong")
    @Size(min = 1, max = 50, message = "Nama genre harus antara 1 dan 50 karakter")
    private String nama;

    private Integer totalFilms;
    
    // Constructors
    public GenreDTO() {}

    public GenreDTO(Long id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    // Custom constructor for report data
    public GenreDTO(Long id, String nama, Integer totalFilms) {
        this.id = id;
        this.nama = nama;
        this.totalFilms = totalFilms;
    }
}