package com.example.project.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import com.example.project.Repository.ReviewRepository;
import com.example.project.Model.Review;
import com.example.project.Model.Product;
import com.example.project.Model.User;
import com.example.project.Repository.UserRepository;
import com.example.project.Repository.ProductRepository;
import com.example.project.DTO.ReviewDTO;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
    }

    public void addReview(Long productId, Long userId, ReviewDTO reviewDTO) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Review review = Review.builder()
            .product(product)
            .user(user)
            .reviewText(reviewDTO.getReviewText())
            .rating(reviewDTO.getRating())
            .reviewDate(LocalDateTime.now())
            .build();
        reviewRepository.save(review);
    }

    public void updateReview(Long reviewId, Long userId, String reviewText, int rating) {
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
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        Review review = findReviewById(reviewId);
        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public ReviewDTO getReviewForEdit(Long reviewId, Long userId) {
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
