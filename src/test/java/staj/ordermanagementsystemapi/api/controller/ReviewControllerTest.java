package staj.ordermanagementsystemapi.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import staj.ordermanagementsystemapi.business.abstracts.ReviewService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;
import staj.ordermanagementsystemapi.entities.dto.ProductDto;
import staj.ordermanagementsystemapi.entities.dto.ReviewDto;

public class ReviewControllerTest {

    private ReviewController reviewController;
    private ReviewService reviewService;

    @BeforeEach
    public void setUp() {
        reviewService = mock(ReviewService.class);
        reviewController = new ReviewController(reviewService);
    }

    @Test
    void getAllReviews_ReturnsAllReviewsSuccessfully() {
        // Arrange
        List<ReviewDto> expectedReviews = new ArrayList<>();
        expectedReviews.add(new ReviewDto(1, "Great product!", 5, new CustomerDto(), new ProductDto(), new Date()));
        expectedReviews.add(new ReviewDto(2, "Good quality.", 4, new CustomerDto(), new ProductDto(), new Date()));
        when(reviewService.getAllReviews()).thenReturn(expectedReviews);

        // Act
        ResponseEntity<List<ReviewDto>> responseEntity = reviewController.getAllReviews();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedReviews.size(), responseEntity.getBody().size());
    }

    @Test
    void getReviewById_ValidReviewId_ReturnsReviewSuccessfully() {
        // Arrange
        int reviewId = 1;
        ReviewDto expectedReview = new ReviewDto(reviewId, "Excellent product!", 5, new CustomerDto(), new ProductDto(), new Date());
        when(reviewService.getReviewById(reviewId)).thenReturn(expectedReview);

        // Act
        ResponseEntity<ReviewDto> responseEntity = reviewController.getReviewById(reviewId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedReview.getId(), responseEntity.getBody().getId());
        assertEquals(expectedReview.getDescription(), responseEntity.getBody().getDescription());
    }

    @Test
    void getReviewById_ReviewNotFound_ReturnsNotFound() {
        // Arrange
        int reviewId = 1;
        when(reviewService.getReviewById(reviewId)).thenReturn(null);

        // Act
        ResponseEntity<ReviewDto> responseEntity = reviewController.getReviewById(reviewId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void addReview_ValidReview_ReturnsCreatedReview() {
        // Arrange
        ReviewDto newReview = new ReviewDto(null, "Nice product!", 4, new CustomerDto(), new ProductDto(), new Date());
        ReviewDto expectedSavedReview = new ReviewDto(1,"Nice product!", 4, new CustomerDto(), new ProductDto(), new Date());
        when(reviewService.saveReview(newReview)).thenReturn(expectedSavedReview);

        // Act
        ResponseEntity<ReviewDto> responseEntity = reviewController.addReview(newReview);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedSavedReview.getId(), responseEntity.getBody().getId());
        assertEquals(expectedSavedReview.getDescription(), responseEntity.getBody().getDescription());
    }

    @Test
    void updateReview_ValidReview_ReturnsUpdatedReview() {
        // Arrange
        int reviewId = 1;
        ReviewDto updatedReview = new ReviewDto(reviewId, "Average product.", 3, new CustomerDto(), new ProductDto(), new Date());
        when(reviewService.updateReview(reviewId, updatedReview.getStar(), updatedReview.getDescription()))
                .thenReturn(updatedReview);

        // Act
        ResponseEntity<ReviewDto> responseEntity = reviewController.updateReview(reviewId, updatedReview.getStar(), updatedReview.getDescription());

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(updatedReview.getId(), responseEntity.getBody().getId());
        assertEquals(updatedReview.getDescription(), responseEntity.getBody().getDescription());
    }

    @Test
    void updateReview_ReviewNotFound_ReturnsNotFound() {
        // Arrange
        int reviewId = 1;
        ReviewDto updatedReview = new ReviewDto(reviewId, "Average product.", 3, new CustomerDto(), new ProductDto(), new Date());
        when(reviewService.updateReview(reviewId, updatedReview.getStar(), updatedReview.getDescription()))
                .thenThrow(new ResourceNotFoundException("Review", "id", reviewId));

        // Act
        ResponseEntity<ReviewDto> responseEntity = reviewController.updateReview(reviewId, updatedReview.getStar(), updatedReview.getDescription());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void deleteReview_ValidReviewId_ReturnsNoContent() {
        // Arrange
        int reviewId = 1;

        // Act
        ResponseEntity<Void> responseEntity = reviewController.deleteReview(reviewId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteReview_ReviewNotFound_ReturnsNotFound() {
        // Arrange
        int reviewId = 1;
        doThrow(new ResourceNotFoundException("Review", "id", reviewId)).when(reviewService).deleteReview(reviewId);

        // Act
        ResponseEntity<Void> responseEntity = reviewController.deleteReview(reviewId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
