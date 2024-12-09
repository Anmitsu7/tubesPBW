package com.example.demo.admin.Controller;

import com.example.demo.admin.Model.Film;
import com.example.demo.admin.Repository.FilmRepository;
import com.example.demo.admin.Model.Genre;
import com.example.demo.admin.Repository.GenreRepository;
import com.example.demo.admin.Model.Aktor;
import com.example.demo.admin.Repository.AktorRepository;
import com.example.demo.admin.Repository.PenyewaanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AktorRepository aktorRepository;

    @Autowired
    private PenyewaanRepository penyewaanRepository;

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Tambahkan data untuk dashboard
        model.addAttribute("totalFilm", filmRepository.count());
        model.addAttribute("sedangDisewa", penyewaanRepository.countByStatus("DISEWA"));
        model.addAttribute("penyewaanBulanIni", penyewaanRepository.countPenyewaanBulanIni());
        model.addAttribute("totalGenre", genreRepository.count());
        model.addAttribute("recentRentals", penyewaanRepository.findRecentRentals());
        return "admin/dashboard";
    }

    // Film Management
    @GetMapping("/films")
    public String filmList(Model model) {
        model.addAttribute("films", filmRepository.findAll());
        model.addAttribute("genres", genreRepository.findAll());
        model.addAttribute("actors", aktorRepository.findAll());
        return "admin/films";
    }

    @PostMapping("/films/add")
    public String addFilm(@RequestParam("judul") String judul,
                         @RequestParam("deskripsi") String deskripsi,
                         @RequestParam("genre_id") Long genreId,
                         @RequestParam("tahun_rilis") Integer tahunRilis,
                         @RequestParam("stok") Integer stok,
                         @RequestParam("cover") MultipartFile cover,
                         @RequestParam("aktor_ids") List<Long> aktorIds) {
        
        Film film = new Film();
        film.setJudul(judul);
        film.setDeskripsi(deskripsi);
        film.setGenre(genreRepository.findById(genreId).orElse(null));
        film.setTahunRilis(tahunRilis);
        film.setStok(stok);

        // Handle file upload
        if (!cover.isEmpty()) {
            try {
                String uploadDir = "uploads/covers/";
                String fileName = System.currentTimeMillis() + "_" + cover.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(cover.getInputStream(), uploadPath.resolve(fileName));
                film.setCoverUrl(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set actors
        film.setActors(new HashSet<>(aktorRepository.findAllById(aktorIds)));
        
        filmRepository.save(film);
        return "redirect:/admin/films";
    }

    // Genre Management
    @GetMapping("/genres")
    public String genreList(Model model) {
        model.addAttribute("genres", genreRepository.findAll());
        return "admin/genres";
    }

    @PostMapping("/genres/add")
    public String addGenre(@RequestParam("nama") String nama) {
        Genre genre = new Genre();
        genre.setNama(nama);
        genreRepository.save(genre);
        return "redirect:/admin/genres";
    }

    // Actor Management
    @GetMapping("/actors")
    public String actorList(Model model) {
        model.addAttribute("actors", aktorRepository.findAll());
        return "admin/actors";
    }

    @PostMapping("/actors/add")
    public String addActor(@RequestParam("nama") String nama,
                          @RequestParam("negara_asal") String negaraAsal,
                          @RequestParam(value = "foto", required = false) MultipartFile foto) {
        
        Aktor aktor = new Aktor();
        aktor.setNama(nama);
        aktor.setNegaraAsal(negaraAsal);

        // Handle file upload if photo is provided
        if (foto != null && !foto.isEmpty()) {
            try {
                String uploadDir = "uploads/actors/";
                String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(foto.getInputStream(), uploadPath.resolve(fileName));
                // Assuming you have a setFotoUrl method in Aktor class
                aktor.setFotoUrl(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        aktorRepository.save(aktor);
        return "redirect:/admin/actors";
    }

    // Reports
    @GetMapping("/reports")
    public String reports(Model model) {
        return "admin/reports";
    }

    @GetMapping("/reports/data")
    @ResponseBody
    public Map<String, Object> getReportData(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        // Get rental statistics
        List<Map<String, Object>> monthlyRentals = penyewaanRepository.getMonthlyRentals(year);
        List<Map<String, Object>> popularMovies = penyewaanRepository.getPopularMovies(year, month);
        Map<String, Object> summary = penyewaanRepository.getRentalSummary(year, month);

        return Map.of(
            "monthlyRentals", monthlyRentals,
            "popularMovies", popularMovies,
            "summary", summary
        );
    }

    // Delete endpoints
    @PostMapping("/films/delete/{id}")
    public ResponseEntity<?> deleteFilm(@PathVariable Long id) {
        filmRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/genres/delete/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        genreRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/actors/delete/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Long id) {
        aktorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}