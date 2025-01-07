package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Booking;
import com.example.demo.model.Film;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.FilmRepository;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private FilmRepository filmRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createBooking(Long filmId, String username, int duration) {
        try {
            logger.info("Creating booking - Film: {}, User: {}, Duration: {}", filmId, username, duration);

            // Get user
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
            logger.info("User found: {}", user.getUsername());

            // Get film
            Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));
            logger.info("Film found: {}", film.getJudul());

            // Check stock
            if (film.getStok() <= 0) {
                logger.warn("Film {} tidak tersedia (stok: {})", film.getJudul(), film.getStok());
                throw new RuntimeException("Film sedang tidak tersedia");
            }

            // Check if already booked
            if (isFilmAlreadyBooked(user, filmId)) {
                logger.warn("Film {} sudah dipinjam oleh user {}", film.getJudul(), username);
                throw new RuntimeException("Film sudah dipinjam");
            }

            // Create new booking
            Booking booking = new Booking();
            booking.setFilm(film);
            booking.setUser(user);
            booking.setDuration(duration);
            booking.setStartDate(LocalDate.now());
            booking.setEndDate(LocalDate.now().plusDays(duration));
            booking.setStatus("DISEWA");
            booking.setLateFee(0.0);
            booking.setNotes("Peminjaman baru");

            // Update film stock
            film.setStok(film.getStok() - 1);
            filmRepository.save(film);
            logger.info("Updated film stock. New stock: {}", film.getStok());

            // Save booking
            Booking savedBooking = bookingRepository.save(booking);
            logger.info("Booking saved with ID: {}", savedBooking.getId());

        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal membuat booking: " + e.getMessage());
        }
    }

    public boolean isFilmAlreadyBooked(User user, Long filmId) {
        return bookingRepository.existsByUserAndFilmIdAndStatus(user, filmId, "DISEWA");
    }

    public List<Booking> getUserActiveBookings(User user) {
        return bookingRepository.findByUserAndStatus(user, "DISEWA");
    }

    public void returnFilm(Long bookingId) {
        try {
            logger.info("Processing return for booking ID: {}", bookingId);

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));

            if (!"DISEWA".equals(booking.getStatus())) {
                logger.warn("Booking {} tidak aktif", bookingId);
                throw new RuntimeException("Booking sudah tidak aktif");
            }

            booking.setStatus("DIKEMBALIKAN");
            booking.setEndDate(LocalDate.now());

            // Tambah stok film
            Film film = booking.getFilm();
            film.setStok(film.getStok() + 1);
            filmRepository.save(film);
            logger.info("Updated film stock to: {}", film.getStok());

            // Hitung keterlambatan
            LocalDate dueDate = booking.getStartDate().plusDays(booking.getDuration());
            if (LocalDate.now().isAfter(dueDate)) {
                long daysLate = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
                double lateFee = daysLate * 10000.0; // Rp 10.000 per hari
                booking.setLateFee(lateFee);
                booking.setNotes("Terlambat " + daysLate + " hari");
                logger.info("Biaya keterlambatan: Rp {}", lateFee);
            }

            bookingRepository.save(booking);
            logger.info("Pengembalian berhasil diproses untuk booking ID: {}", bookingId);

        } catch (Exception e) {
            logger.error("Error saat memproses pengembalian: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal memproses pengembalian: " + e.getMessage());
        }
    }
}