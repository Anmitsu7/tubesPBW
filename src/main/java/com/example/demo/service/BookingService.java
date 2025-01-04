package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Booking;
import com.example.demo.model.Film;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.FilmRepository;
@Service
@Transactional
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private FilmRepository filmRepository;
    
    public void createBooking(Long filmId, String username, int duration) {
        Film film = filmRepository.findById(filmId)
            .orElseThrow(() -> new RuntimeException("Film tidak ditemukan"));
            
        if (film.getStok() <= 0) {
            throw new RuntimeException("Film sedang tidak tersedia");
        }
        
        Booking booking = new Booking();
        booking.setFilm(film);
        booking.setUsername(username);
        booking.setDuration(duration);
        booking.setStartDate(LocalDate.now());
        booking.setEndDate(LocalDate.now().plusDays(duration));
        booking.setStatus("ACTIVE");
        
        // Kurangi stok film
        film.setStok(film.getStok() - 1);
        filmRepository.save(film);
        
        // Simpan booking
        bookingRepository.save(booking);
        
        
    }
    
    public List<Booking> getUserActiveBookings(String username) {
        return bookingRepository.findByUsernameAndStatus(username, "ACTIVE");
    }

    public boolean isFilmAlreadyBooked(String username, Long filmId) {
        return bookingRepository.existsByUsernameAndFilmIdAndStatus(username, filmId, "ACTIVE");
    }

    public boolean hasActiveBooking(String username, Long filmId) {
    return bookingRepository.existsByUsernameAndFilmIdAndStatus(username, filmId, "ACTIVE");
}

    
    
    public void returnFilm(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking tidak ditemukan"));
            
        if (!"ACTIVE".equals(booking.getStatus())) {
            throw new RuntimeException("Booking sudah tidak aktif");
        }
        
        booking.setStatus("RETURNED");
        booking.setEndDate(LocalDate.now());
        
        // Tambah stok film
        Film film = booking.getFilm();
        film.setStok(film.getStok() + 1);
        filmRepository.save(film);
        
        bookingRepository.save(booking);
    }
}