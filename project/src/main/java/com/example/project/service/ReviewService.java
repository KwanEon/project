package com.example.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.example.project.dto.ReviewDTO;
import com.example.project.model.Product;
import com.example.project.model.Review;
import com.example.project.model.User;
import com.example.project.repository.ProductRepository;
import com.example.project.repository.ReviewRepository;
import com.example.project.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Review findReviewById(Long reviewId) {   // 리뷰 조회
        return reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    }

    public void addReview(Long productId, Long userId, ReviewDTO reviewDTO) {   // 리뷰 추가
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Review review = Review.builder()
            .user(user)
            .reviewText(reviewDTO.getReviewText())
            .rating(reviewDTO.getRating())
            .build();

        user.addReview(review); // 양방향 연관관계 설정
        product.addReview(review);
        reviewRepository.save(review);
    }

    public void updateReview(Long reviewId, Long userId, String reviewText, int rating) {   // 리뷰 수정
        Review review = findReviewById(reviewId);

        if (!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1에서 5 사이여야 합니다.");
        }
        if (reviewText == null || reviewText.trim().isEmpty()) {
            throw new IllegalArgumentException("리뷰 내용은 비워둘 수 없습니다.");
        }

        review.setReviewText(reviewText);
        review.setRating(rating);
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {   // 리뷰 삭제
        Review review = findReviewById(reviewId);
        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public ReviewDTO getReviewForEdit(Long reviewId, Long userId) {  // 리뷰 수정용 DTO 조회
        Review review = findReviewById(reviewId);

        // 작성자 본인인지 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("본인이 작성한 리뷰만 조회할 수 있습니다.");
        }

        // 반환용 DTO로 변환
        return ReviewDTO.builder()
                .id(review.getId())
                .reviewText(review.getReviewText())
                .rating(review.getRating())
                .build();
    }
}
