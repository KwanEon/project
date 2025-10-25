package com.example.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.project.model.Order;
import com.example.project.model.OrderItem;
import com.example.project.model.Product;
import com.example.project.repository.OrderItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItem createOrderItem(Order order, Product product, int quantity) {   // 주문 항목 생성
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        OrderItem orderItem = OrderItem.builder()
            .product(product)
            .quantity(quantity)
            .price(product.getPrice() * quantity)
            .build();

        order.addOrderItem(orderItem); // 양방향 연관관계 설정
        return orderItem;
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {   // 주문 ID로 주문 항목 조회
        return orderItemRepository.findByOrderId(orderId);
    }
}
