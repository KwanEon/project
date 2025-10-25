package com.example.project.repository;

import org.springframework.stereotype.Repository;

import com.example.project.model.Product;
import com.example.project.model.Product.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findByCategoryAndNameContainingIgnoreCase(Category category, String name, Pageable pageable);
}
