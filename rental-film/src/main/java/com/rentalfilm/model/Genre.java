package com.rentalfilm.model;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "genre")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama genre tidak boleh kosong")
    @Column(nullable = false, unique = true, length = 50)
    private String nama;

    @Column(length = 200)
    private String deskripsi;

    @OneToMany(mappedBy = "genre")
    private List<Film> films;

    public String getNama() {
        return this.nama;
    }

    // Konstruktor, getter, dan setter
}