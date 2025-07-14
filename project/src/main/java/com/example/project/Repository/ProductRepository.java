package com.example.project.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.Model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.project.Model.Product.Category;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByCategory(Category category);
    List<Product> findByCategoryAndNameContainingIgnoreCase(Category category, String name);
    @Query("select p from Product p " +
           "left join fetch p.reviews " +
           "where p.id = :id")
    Optional<Product> findByIdWithReviews(@Param("id") Long id);
}
