package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.review.CreateReviewRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.review.ReviewResponseDTO;

public interface ReviewRestService {
    
    // Get all reviews by property ID
    List<ReviewResponseDTO> getReviewsByPropertyID(UUID propertyID) throws AccessDeniedException;
    
    // Get all reviews by customer ID
    List<ReviewResponseDTO> getReviewsByCustomerID(UUID customerID) throws AccessDeniedException;
    
    // Get review detail by review ID
    ReviewResponseDTO getReviewByID(UUID reviewID) throws AccessDeniedException;
    
    // Create new review
    ReviewResponseDTO createReview(CreateReviewRequestDTO dto) throws AccessDeniedException;
}
