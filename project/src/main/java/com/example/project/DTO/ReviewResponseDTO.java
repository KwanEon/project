package com.example.project.dto;

import java.time.LocalDateTime;

import com.example.project.model.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDTO {
    private Long id;
    private String reviewText;
    private int rating;
    private String reviewer;
    private LocalDateTime reviewDate;

    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .id(review.getId())
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .reviewer(review.getUser().getUsername())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
