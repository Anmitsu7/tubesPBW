package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.Genre;
import com.example.demo.service.ProductService;
import com.example.demo.service.GenreService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final GenreService genreService;

    public ProductController(ProductService productService, GenreService genreService) {
        this.productService = productService;
        this.genreService = genreService;
    }

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String judul,
            @RequestParam(required = false, defaultValue = "0") int tahunMin,
            @RequestParam(required = false, defaultValue = "9999") int tahunMax,
            @RequestParam(required = false, defaultValue = "0") int stokMin,
            @RequestParam(required = false) List<Long> genres,
            Model model,
            Authentication authentication) {
        if (authentication != null) {
            List<Product> searchResults = productService.searchProducts(judul, tahunMin, tahunMax, stokMin, genres);
            List<Genre> allGenres = genreService.getAllGenres();

            model.addAttribute("username", authentication.getName());
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("genres", allGenres);
            return "product-search-results"; // Template untuk hasil pencarian produk
        }
        return "redirect:/login";
    }
}
