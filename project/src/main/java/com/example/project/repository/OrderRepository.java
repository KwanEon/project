package com.example.project.repository;

import org.springframework.stereotype.Repository;

import com.example.project.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findByUserId(Long userId);
}
