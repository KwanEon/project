package com.example.project.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.project.Repository.OrderItemRepository;
import com.example.project.Model.Product;
import com.example.project.Model.Order;
import com.example.project.Model.OrderItem;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public void createOrderItem(Order order, Product product, int quantity) {   // 주문 항목 생성
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        OrderItem orderItem = OrderItem.builder()
            .order(order)
            .product(product)
            .quantity(quantity)
            .price(product.getPrice() * quantity)
            .build();
        orderItemRepository.save(orderItem);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {   // 주문 ID로 주문 항목 조회
        return orderItemRepository.findByOrderId(orderId);
    }
}
