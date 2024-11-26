package com.rentalfilm.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "pengguna")
public class Pengguna {
    public enum Role {
        ADMIN, PELANGGAN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Username harus antara 3-50 karakter")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Column(nullable = false)
    private String password;

    @Email(message = "Email tidak valid")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "nama_lengkap")
    private String namaLengkap;

    @Column(name = "nomor_telepon")
    private String nomorTelepon;

    @Column(name = "tanggal_registrasi")
    private LocalDateTime tanggalRegistrasi;

    @OneToMany(mappedBy = "pengguna")
    private List<Penyewaan> penyewaan;

    // Konstruktor, getter, dan setter
}