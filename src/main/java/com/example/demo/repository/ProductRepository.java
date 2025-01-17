package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByJudulContainingIgnoreCaseAndTahunRilisBetweenAndStokGreaterThanEqual(
            String judul, int tahunMin, int tahunMax, int stokMin);

    List<Product> findByJudulContainingIgnoreCaseAndTahunRilisBetweenAndStokGreaterThanEqualAndGenresIn(
            String judul, int tahunMin, int tahunMax, int stokMin, List<Long> genres);
}