package com.example.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;

    @NotBlank(message = "리뷰 내용을 입력하세요.")
    @Size(min = 1, max = 500, message = "리뷰 내용은 1자 이상 500자 이하로 입력해주세요.")
    private String reviewText;

    @NotNull(message = "리뷰 점수를 입력하세요.")
    @Min(value = 1, message = "최소 1점 이상이어야 합니다.")
    @Max(value = 5, message = "최대 5점 이하이어야 합니다.")
    private int rating;
}
