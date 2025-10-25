package com.example.project.repository;

import org.springframework.stereotype.Repository;

import com.example.project.model.Review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
    List<Review> findByUserId(Long userId);
}
