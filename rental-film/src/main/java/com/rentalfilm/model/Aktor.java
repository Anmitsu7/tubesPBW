package com.rentalfilm.model;

import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "aktor")
public class Aktor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama aktor tidak boleh kosong")
    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Size(max = 50, message = "Negara asal maksimal 50 karakter")
    @Column(name = "negara_asal")
    private String negaraAsal;

    @Column(name = "tanggal_lahir")
    private LocalDateTime tanggalLahir;

    @ManyToMany(mappedBy = "aktor")
    private Set<Film> films;

    // Konstruktor, getter, dan setter
    public Aktor() {}

    public Aktor(String nama, String negaraAsal) {
        this.nama = nama;
        this.negaraAsal = negaraAsal;
    }

    // Getter dan setter
}