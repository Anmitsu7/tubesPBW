package com.rentalfilm.repository;

import com.rentalfilm.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PenyewaanRepository extends JpaRepository<Penyewaan, Long> {
    // Cari penyewaan berdasarkan status
    List<Penyewaan> findByStatus(Penyewaan.Status status);

    // Cari penyewaan untuk pengguna tertentu
    List<Penyewaan> findByPenggunaId(Long penggunaId);

    // Cari penyewaan untuk film tertentu
    List<Penyewaan> findByFilmId(Long filmId);

    // Penyewaan yang belum dikembalikan
    @Query("SELECT p FROM Penyewaan p WHERE p.status = 'DISEWA'")
    List<Penyewaan> findActivePenyewaan();

    // Penyewaan yang terlambat
    @Query("SELECT p FROM Penyewaan p " +
           "WHERE p.status = 'DISEWA' AND p.tanggalJatuhTempo < CURRENT_TIMESTAMP")
    List<Penyewaan> findOverduePenyewaan();

    // Statistik penyewaan bulanan
    @Query("SELECT FUNCTION('MONTH', p.tanggalSewa), " +
           "FUNCTION('YEAR', p.tanggalSewa), " +
           "COUNT(p), " +
           "SUM(p.totalBiaya) " +
           "FROM Penyewaan p " +
           "GROUP BY FUNCTION('MONTH', p.tanggalSewa), FUNCTION('YEAR', p.tanggalSewa)")
    List<Object[]> getPenyewaanMonthlyStatistics();

    // Total pendapatan dari penyewaan dalam rentang waktu
    @Query("SELECT SUM(p.totalBiaya) FROM Penyewaan p " +
           "WHERE p.tanggalSewa BETWEEN :startDate AND :endDate")
    Double getTotalPendapatanBetweenDates(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
}