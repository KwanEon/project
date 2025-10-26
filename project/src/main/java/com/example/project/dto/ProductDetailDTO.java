package com.example.project.dto;

import java.util.ArrayList;
import java.util.List;
import com.example.project.model.Product.Category;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private String description;
    private double price;
    private int stock;
    private Category category;

    @Builder.Default
    private final List<ReviewResponseDTO> reviews = new ArrayList<>();
}
