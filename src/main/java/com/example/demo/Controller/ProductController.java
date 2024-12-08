package com.example.demo.Controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam String judul,
            @RequestParam int tahunMin,
            @RequestParam int tahunMax,
            @RequestParam int stokMin) {
        return productService.searchProducts(judul, tahunMin, tahunMax, stokMin);
    }
}
