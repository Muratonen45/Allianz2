package staj.ordermanagementsystemapi.business.abstracts;

import java.util.List;

import staj.ordermanagementsystemapi.entities.dto.ReviewDto;

public interface ReviewService {
    List<ReviewDto> getAllReviews();
    ReviewDto getReviewById(Integer id);
    ReviewDto saveReview(ReviewDto reviewDTO);
    ReviewDto updateReview(Integer id, Integer updatedStar, String updatedDetails);
    void deleteReview(Integer id);

    double getAverageReviewOfProduct(Integer productId);

    List<ReviewDto> getReviewsOfCustomer(Integer customerId);

    List<ReviewDto> getReviewsOfProduct(Integer productId);

}
