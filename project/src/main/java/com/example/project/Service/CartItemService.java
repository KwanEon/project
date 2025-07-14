package com.example.project.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.project.Repository.CartItemRepository;
import com.example.project.DTO.CartItemDTO;
import com.example.project.Model.CartItem;
import com.example.project.Model.User;
import com.example.project.Repository.UserRepository;
import com.example.project.Model.Product;
import com.example.project.Repository.ProductRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addCartItem(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        else {
            cartItem = CartItem.builder()
                .product(product)
                .user(user)
                .quantity(quantity)
                .build();
        }
        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItemDTOsByUserId(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return cartItems.stream()
            .map(cartItem -> new CartItemDTO(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity()
            ))
            .toList();
    }

    public void updateCartItem(Long id, int change) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        int newQuantity = cartItem.getQuantity() + change;
        if (newQuantity < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        cartItem.setQuantity(newQuantity);
        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(Long userid) {
        cartItemRepository.deleteByUserId(userid);
    }

    public void deleteCartItemByProductId(Long userId, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem == null) {
            throw new IllegalArgumentException("장바구니에 해당 상품이 없습니다.");
        }
        cartItemRepository.delete(cartItem);
    }

    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
    }
}
