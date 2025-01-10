

package com.example.demo.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    private Long id;
    private String filmTitle;
    private String username;
    private LocalDate tanggalSewa;
    private LocalDate tanggalKembali;
    private String status;
    private Double lateFee;
    private Boolean isOverdue;
    private String notes;
    
    // Constructors, getters, setters

    public RentalDTO(String filmTitle, Long id, Boolean isOverdue, Double lateFee, String notes, String status, LocalDate tanggalKembali, LocalDate tanggalSewa, String username) {
        this.filmTitle = filmTitle;
        this.id = id;
        this.isOverdue = isOverdue;
        this.lateFee = lateFee;
        this.notes = notes;
        this.status = status;
        this.tanggalKembali = tanggalKembali;
        this.tanggalSewa = tanggalSewa;
        this.username = username;
    }
    
}