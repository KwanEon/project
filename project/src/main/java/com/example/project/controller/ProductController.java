package com.example.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.example.project.dto.ProductDTO;
import com.example.project.dto.ProductListDTO;
import com.example.project.dto.ReviewDTO;
import com.example.project.model.Product;
import com.example.project.model.Product.Category;
import com.example.project.repository.ReviewRepository;
import com.example.project.security.CustomUserDetails;
import com.example.project.service.ProductService;
import com.example.project.service.ReviewService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @GetMapping // 상품 목록 조회
    public ResponseEntity<Page<ProductListDTO>> getProducts(@RequestParam(value = "category", required = false) Category category,
                                                        @RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "page", defaultValue = "0") int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Product> products;

        if (category != null && (keyword == null || keyword.isEmpty())) {
            products = productService.searchProductsByCategory(category, pageRequest);
        } else if (category == null && keyword != null && !keyword.isEmpty()) {
            products = productService.searchProductsByName(keyword, pageRequest);
        } else if (category != null && keyword != null && !keyword.isEmpty()) {
            products = productService.searchProductsByNameAndCategory(category, keyword, pageRequest);
        } else {
            products = productService.getAllProducts(pageRequest);
        }
        Page<ProductListDTO> productListDTOs = products.map(productService::convertToListDTO);
        return ResponseEntity.ok(productListDTOs);
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
        return ResponseEntity.status(HttpStatus.CREATED).body("상품 추가 완료.");
    }

    @PutMapping("/{productId}") // 상품 수정
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateProduct(@PathVariable("productId") Long productId,
                                                @RequestBody @Valid ProductDTO productDTO) {
        productService.updateProduct(productId, productDTO);
        return ResponseEntity.ok("상품 업데이트 완료.");
    }

    @DeleteMapping("/{productId}") // 상품 삭제
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("상품 삭제 완료.");
    }
}
