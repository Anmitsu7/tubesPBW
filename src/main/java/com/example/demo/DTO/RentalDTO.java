

package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long filmId;
    private String filmTitle;
    private LocalDate tanggalSewa;
    private LocalDate tanggalKembali;
    private String status;
    private Integer rentalDuration;
    private BigDecimal lateFee;
    private String notes;
}