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

    public List<Product> searchProducts(String judul, int tahunMin, int tahunMax, int stokMin) {
        return productRepository.findByJudulContainingIgnoreCaseAndTahunRilisBetweenAndStokGreaterThanEqual(
            judul, tahunMin, tahunMax, stokMin);
    }
}
