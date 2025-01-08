package com.example.demo.repository;

import com.example.demo.model.Booking;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.status = :status")
    List<Booking> findByUserAndStatus(@Param("user") User user, @Param("status") String status);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user = :user AND b.film.id = :filmId AND b.status = :status")
    boolean existsByUserAndFilmIdAndStatus(
            @Param("user") User user,
            @Param("filmId") Long filmId,
            @Param("status") String status);

    long countByUser(User user);

    long countByUserAndStatus(User user, String status);

    @Query(value = "SELECT * FROM penyewaan b WHERE b.pengguna_id = :#{#user.id} ORDER BY b.tanggal_sewa DESC LIMIT 5", 
           nativeQuery = true)
    List<Booking> findRecentBookings(@Param("user") User user);
}