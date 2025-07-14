package com.example.project.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.project.Repository.OrderRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Repository.ProductRepository;
import com.example.project.Repository.ReviewRepository;
import com.example.project.Repository.OrderItemRepository;
import com.example.project.Model.CartItem;
import com.example.project.Model.Order;
import com.example.project.Model.Order.OrderStatus;
import com.example.project.Model.OrderItem;
import com.example.project.Model.Product;
import com.example.project.Model.User;
import com.example.project.Model.Review;
import com.example.project.DTO.OrderDTO;
import com.example.project.DTO.OrderItemDTO;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;
    private final CartItemService cartItemService;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public Order placeOrder(Long userId) {      // 주문 생성(장바구니)
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));
        List<CartItem> cartItems = cartItemService.getCartItemsByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다. 주문할 항목이 없습니다.");
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(Order.OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        // 총 금액 계산
        double totalPrice = 0;

        // 각 장바구니 항목을 주문 항목으로 변환
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            double itemPrice = product.getPrice() * quantity;

            if (product.getStock() < quantity) {
                throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + product.getStock());
            }

            totalPrice += itemPrice;
            
            orderItemService.createOrderItem(order, product, quantity);
            product.setStock(product.getStock() - quantity); // 재고 차감
            productRepository.save(product); // 재고 업데이트
        }

        // 총 금액 설정
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);

        // 장바구니 비우기
        cartItemService.deleteCartItem(userId);

        return order;
    }

    public Order placeIndividualOrder(Long userId, Long productId, int quantity) {  // 개별 상품 주문 생성
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }

        // 사용자 확인
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        // 상품 확인
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));

        // 재고 확인
        if (product.getStock() < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + product.getStock());
        }

        // 총 금액 계산
        double totalPrice = product.getPrice() * quantity;

        // 주문 생성
        Order order = Order.builder()
            .user(user)
            .orderDate(LocalDateTime.now())
            .status(OrderStatus.PENDING)
            .totalPrice(totalPrice)
            .build();
        orderRepository.save(order);

        // 주문 항목 생성
        orderItemService.createOrderItem(order, product, quantity);

        // 재고 차감
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        return order;
    }


    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {   // 사용자별 주문 목록 조회
        Map<Long, Long> myReviewMap = reviewRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(r -> r.getProduct().getId(), Review::getId));

        List<OrderDTO> orderDTOs = orderRepository.findByUserId(userId).stream()
                .map((Order order) -> OrderDTO.builder()
                        .id(order.getId())
                        .userId(order.getUser().getId())
                        .orderDate(order.getOrderDate().toString())
                        .status(order.getStatus().name())
                        .totalPrice(order.getTotalPrice())
                        .orderItems(order.getOrderItems().stream()
                                .map((OrderItem orderItem) -> OrderItemDTO.builder()
                                        .id(orderItem.getId())
                                        .orderId(order.getId())
                                        .productId(orderItem.getProduct().getId())
                                        .productName(orderItem.getProduct().getName())
                                        .quantity(orderItem.getQuantity())
                                        .price(orderItem.getPrice())
                                        .reviewId(myReviewMap.getOrDefault(orderItem.getProduct().getId(), null)) // 리뷰 ID 추가
                                        .build())
                                .toList())
                        .build())
                .toList();
        return orderDTOs;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {   // 주문 상세 조회
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다"));
    }

    public void cancelOrder(Long orderId) {     // 주문 취소
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("이미 배송된 주문은 취소할 수 없습니다.");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 주문 항목의 재고 복구
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity()); // 재고 복구
            productRepository.save(product);
        }
    }
}
