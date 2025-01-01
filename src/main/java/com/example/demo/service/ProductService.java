package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchProducts(String judul, int tahunMin, int tahunMax, int stokMin, List<Long> genres) {
        if (genres == null || genres.isEmpty()) {
            return productRepository.findByJudulContainingIgnoreCaseAndTahunRilisBetweenAndStokGreaterThanEqual(
                judul, tahunMin, tahunMax, stokMin);
        }
        return productRepository.findByJudulContainingIgnoreCaseAndTahunRilisBetweenAndStokGreaterThanEqualAndGenresIn(
            judul, tahunMin, tahunMax, stokMin, genres);
    }
}
