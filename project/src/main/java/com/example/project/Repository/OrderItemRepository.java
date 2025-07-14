package com.example.project.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.Model.OrderItem;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 주문 항목을 주문 ID로 조회
    List<OrderItem> findByOrderId(Long orderId);
}
