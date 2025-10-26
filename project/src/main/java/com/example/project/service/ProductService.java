package com.example.project.service;

import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.project.dto.ProductDTO;
import com.example.project.dto.ProductListDTO;
import com.example.project.dto.ProductDetailDTO;
import com.example.project.dto.ReviewResponseDTO;
import com.example.project.model.Product;
import com.example.project.model.Product.Category;
import com.example.project.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {    // 상품 상세 조회
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {    // 모든 상품 조회(페이징)
        return productRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productRepository.findProductDetailById(id).orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return ProductDetailDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .reviews(product.getReviews().stream()
                        .<ReviewResponseDTO>map(r -> ReviewResponseDTO.builder()
                                .id(r.getId())
                                .reviewer(r.getUser().getName())
                                .reviewText(r.getReviewText())
                                .rating(r.getRating())
                                .reviewDate(r.getReviewDate())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void addProduct(ProductDTO productDTO) {   // 상품 추가
        Product product = Product.builder()
                .name(productDTO.getName())
                .imageUrl("/static/images/" + productDTO.getName() + ".jpg")
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .category(productDTO.getCategory())
                .build();
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(Long id, ProductDTO productDTO) {  // 상품 수정
        Product product = getProductById(id);
        product.setName(productDTO.getName());
        product.setImageUrl("/static/images/" + productDTO.getName() + ".jpg");
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(productDTO.getCategory());
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id) {    // 상품 삭제
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProductsByName(String keyword, Pageable pageable) {  // 상품명으로 검색
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProductsByCategory(Category category, Pageable pageable) {   // 카테고리로 검색
        return productRepository.findByCategory(category, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Product> searchProductsByNameAndCategory(Category category, String keyword, Pageable pageable) {    // 카테고리 + 상품명으로 검색
        return productRepository.findByCategoryAndNameContainingIgnoreCase(category, keyword, pageable);
    }

    public ProductListDTO convertToListDTO(Product product) {   // 상품 리스트용 DTO 변환
        ProductListDTO dto = ProductListDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
        return dto;
    }
}
