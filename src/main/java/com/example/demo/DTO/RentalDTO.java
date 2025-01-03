

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

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