package com.example.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import com.example.project.dto.CartItemDTO;
import com.example.project.dto.OrderDTO;
import org.springframework.web.bind.annotation.*;
import com.example.project.security.CustomUserDetails;
import com.example.project.service.CartItemService;
import com.example.project.service.OrderService;
import com.example.project.model.Order;
import java.util.List;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartItemService cartItemService;

    @GetMapping("/cartitem")    // 장바구니 아이템 목록 조회
    public ResponseEntity<?> showCartItems(@AuthenticationPrincipal CustomUserDetails principal) {
        List<CartItemDTO> cartItems = cartItemService.getCartItemDTOsByUserId(principal.getUserId());
        if (cartItems.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());  // 장바구니가 비어있을 경우 빈 리스트 반환
        }
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/cartitem/{productId}")   // 장바구니에 아이템 추가
    public ResponseEntity<?> addCartItem(@AuthenticationPrincipal CustomUserDetails principal,
                                         @PathVariable("productId") Long productId,
                                         @RequestParam("quantity") int quantity) {
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        cartItemService.addCartItem(principal.getUserId(), productId, quantity);
        return ResponseEntity.ok("상품이 장바구니에 추가되었습니다.");
    }

    @PatchMapping("/cartitem/{cartId}") // 장바구니 아이템 수량 변경
    public ResponseEntity<?> updateCartItemQuantity(@AuthenticationPrincipal CustomUserDetails principal,
                                                    @PathVariable("cartId") Long cartId,
                                                    @RequestParam("operation") String operation) {
        if (cartItemService.findById(cartId).getUser().getId() != principal.getUserId()) {
            return ResponseEntity.status(403).body("권한이 없습니다. 해당 장바구니 상품을 수정할 수 없습니다.");
        }
        if (!operation.equals("increase") && !operation.equals("decrease")) {   // 수량 변경 작업이 'increase' 또는 'decrease'가 아닐 경우
            return ResponseEntity.badRequest().body("잘못된 요청입니다. 'increase' 또는 'decrease'만 허용됩니다.");
        }
        int change = operation.equals("increase") ? 1 : -1; // 수량 변경 값 설정
        cartItemService.updateCartItem(cartId, change);
        return ResponseEntity.ok("장바구니 상품 수량이 업데이트되었습니다.");
    }

    @DeleteMapping("/cartitem")   // 장바구니 전체 삭제
    public ResponseEntity<?> deleteCartItem(@AuthenticationPrincipal CustomUserDetails principal) {
        cartItemService.deleteAllCartItemsByUserId(principal.getUserId());
        return ResponseEntity.ok("장바구니가 비워졌습니다.");
    }

    @DeleteMapping("/cartitem/{productId}") // 장바구니에서 특정 아이템 삭제
    public ResponseEntity<?> deleteCartItemByProductId(@AuthenticationPrincipal CustomUserDetails principal,
                                                       @PathVariable("productId") Long productId) {
        cartItemService.deleteCartItemByProductId(principal.getUserId(), productId);
        return ResponseEntity.ok("상품이 장바구니에서 삭제되었습니다.");
    }

    @PostMapping("/order/cartitem") // 장바구니 아이템으로 주문 생성
    public ResponseEntity<?> placeCartOrder(@AuthenticationPrincipal CustomUserDetails principal) {
        Order order = orderService.placeOrder(principal.getUserId());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/order/{productId}")  // 개별 상품 주문 생성
    public ResponseEntity<?> placeItemOrder(@AuthenticationPrincipal CustomUserDetails principal,
                                            @PathVariable("productId") Long productId,
                                            @RequestParam("quantity") int quantity) {
        Order order = orderService.placeIndividualOrder(principal.getUserId(), productId, quantity);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/auth/orders")   // 현재 로그인한 유저의 주문 목록 조회
    public ResponseEntity<?> getCurrentUserOrders(@AuthenticationPrincipal CustomUserDetails principal) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(principal.getUserId());
        return ResponseEntity.ok(orders);
    }
}
