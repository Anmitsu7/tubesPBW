package com.example.demo.controller;
// package com.example.demo.controller;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.example.demo.dto.FilmDTO;
// import com.example.demo.service.FilmService;
// import com.example.demo.service.GenreService;

// @Controller
// public class ExploreController {
//     private final FilmService filmService;
//     private final GenreService genreService;

//     public ExploreController(FilmService filmService, GenreService genreService) {
//         this.filmService = filmService;
//         this.genreService = genreService;
//     }

//     @GetMapping("/explore")
//     public String explorePage(
//             @RequestParam(required = false) String judul,
//             @RequestParam(required = false) Long genreId,
//             @RequestParam(required = false) Integer tahunRilis,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "12") int size,
//             Model model
//     ) {
//         PageRequest pageable = PageRequest.of(page, size);
//         Page<FilmDTO> searchResults = filmService.searchFilmsAdvanced(
//             judul, 
//             genreId, 
//             tahunRilis, 
//             null, // We're not using the available filter in this UI
//             pageable
//         );
        
//         model.addAttribute("genres", genreService.getAllGenres());
//         model.addAttribute("films", searchResults.getContent());
//         model.addAttribute("currentPage", page);
//         model.addAttribute("totalPages", searchResults.getTotalPages());
//         model.addAttribute("searchJudul", judul);
//         model.addAttribute("searchGenreId", genreId);
//         model.addAttribute("searchTahunRilis", tahunRilis);
        
//         return "explore";
//     }
// }