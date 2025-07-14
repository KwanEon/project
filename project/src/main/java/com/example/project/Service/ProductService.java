package com.example.project.Service;

import org.springframework.stereotype.Service;
import com.example.project.Model.Product.Category;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.project.Repository.ProductRepository;
import com.example.project.Model.Product;
import com.example.project.DTO.ProductDTO;
import com.example.project.DTO.ReviewResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void addProduct(ProductDTO productDTO) {
        Product product = Product.builder()
                .name(productDTO.getName())
                .imageUrl("/static/images/" + productDTO.getName() + ".jpg")
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .stock(productDTO.getStock())
                .category(productDTO.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다"));
        product.setName(productDTO.getName());
        product.setImageUrl("/static/images/" + productDTO.getName() + ".jpg");
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setCategory(productDTO.getCategory());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다"));
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
    
    @Transactional(readOnly = true)
    public List<Product> searchProductsByNameAndCategory(Category category, String keyword) {
        return productRepository.findByCategoryAndNameContainingIgnoreCase(category, keyword);
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO dto = ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .reviews(
                    product.getReviews().stream()
                           .map(ReviewResponseDTO::from)
                           .toList()
                )
                .build();
        return dto;
    }

    public List<ProductDTO> convertToDTOList(List<Product> products) {
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }
}
