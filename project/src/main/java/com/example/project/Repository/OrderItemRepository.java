package com.example.project.repository;

import org.springframework.stereotype.Repository;

import com.example.project.model.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 주문 항목을 주문 ID로 조회
    List<OrderItem> findByOrderId(Long orderId);
}
