package com.example.project.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.project.Service.ProductService;
import com.example.project.Service.ReviewService;
import com.example.project.Repository.ReviewRepository;
import com.example.project.Model.Product;
import com.example.project.DTO.ProductDTO;
import com.example.project.DTO.ReviewDTO;
import com.example.project.Security.CustomUserDetails;
import com.example.project.Model.Product.Category;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @GetMapping // 상품 목록 조회
    public ResponseEntity<List<ProductDTO>> getProducts(@RequestParam(value = "category", required = false) Category category,
                                                        @RequestParam(value = "keyword", required = false) String keyword) {
        List<Product> products;
        if (category != null && (keyword == null || keyword.isEmpty())) {
            products = productService.searchProductsByCategory(category);
        } else if (category == null && keyword != null && !keyword.isEmpty()) {
            products = productService.searchProductsByName(keyword);
        } else if (category != null && keyword != null && !keyword.isEmpty()) {
            products = productService.searchProductsByNameAndCategory(category, keyword);
        } else {
            products = productService.getAllProducts();
        }
        List<ProductDTO> productDTOs = productService.convertToDTOList(products);
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/{productId}") // 상품 상세 조회
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("productId") Long productId) {
        Product product = productService.getProductById(productId);
        ProductDTO productDTO = productService.convertToDTO(product);
        return ResponseEntity.ok(productDTO);
    }

    @PostMapping("/{productId}/reviews") // 상품 리뷰 추가
    public ResponseEntity<String> addReview(@PathVariable("productId") Long productId,
                                            @RequestBody @Valid ReviewDTO reviewDTO,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        if (reviewRepository.existsByProductIdAndUserId(productId, principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 리뷰를 작성한 상품입니다.");
        }
        reviewService.addReview(productId, principal.getUserId(), reviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰 추가 성공");
    }

    @PutMapping("/reviews/{reviewId}") // 상품 리뷰 수정
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId,
                                               @RequestBody @Valid ReviewDTO reviewDTO,
                                               @AuthenticationPrincipal CustomUserDetails principal) {
        reviewService.updateReview(reviewId, principal.getUserId(), reviewDTO.getReviewText(), reviewDTO.getRating());
        return ResponseEntity.ok("리뷰 수정 성공");
    }

    @GetMapping("/reviews/{reviewId}")  // 상품 리뷰 조회
    public ResponseEntity<ReviewDTO> getReview(@PathVariable("reviewId") Long reviewId,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        ReviewDTO dto = reviewService.getReviewForEdit(reviewId, principal.getUserId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping // 상품 추가
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addProduct(@RequestBody @Valid ProductDTO productDTO) {
        productService.addProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product added successfully");
    }

    @PutMapping("/{productId}") // 상품 수정
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateProduct(@PathVariable("productId") Long productId,
                                                @RequestBody @Valid ProductDTO productDTO) {
        productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok("Product updated successfully");
    }

    @DeleteMapping("/{productId}") // 상품 삭제
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }
}
