package com.example.project.Repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.Model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
    List<Review> findByUserId(Long userId);
}
