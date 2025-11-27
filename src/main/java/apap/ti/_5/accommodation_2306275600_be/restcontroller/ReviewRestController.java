package apap.ti._5.accommodation_2306275600_be.restcontroller;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.review.CreateReviewRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.ReviewRestService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewRestController {
    
    @Autowired
    private ReviewRestService reviewRestService;

    // Get all reviews by property ID
    @GetMapping("/property/{propertyID}")
    public ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> getReviewsByProperty(
            @PathVariable UUID propertyID) {
        
        var baseResponseDTO = new BaseResponseDTO<List<ReviewResponseDTO>>();
        
        try {
            List<ReviewResponseDTO> reviews = reviewRestService.getReviewsByPropertyID(propertyID);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(reviews);
            baseResponseDTO.setMessage("Successfully retrieved " + reviews.size() + " review(s) for property");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (AccessDeniedException e) {
            baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
            
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Error: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all reviews by customer ID
    @GetMapping("/customer/{customerID}")
    public ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> getReviewsByCustomer(
            @PathVariable UUID customerID) {
        
        var baseResponseDTO = new BaseResponseDTO<List<ReviewResponseDTO>>();
        
        try {
            List<ReviewResponseDTO> reviews = reviewRestService.getReviewsByCustomerID(customerID);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(reviews);
            baseResponseDTO.setMessage("Successfully retrieved " + reviews.size() + " review(s) for customer");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (AccessDeniedException e) {
            baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
            
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Error: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get review detail by ID
    @GetMapping("/{reviewID}")
    public ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> getReviewDetail(
            @PathVariable UUID reviewID) {
        
        var baseResponseDTO = new BaseResponseDTO<ReviewResponseDTO>();
        
        try {
            ReviewResponseDTO review = reviewRestService.getReviewByID(reviewID);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(review);
            baseResponseDTO.setMessage("Successfully retrieved review detail");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (AccessDeniedException e) {
            baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
            
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Error: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Create new review
    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> createReview(
            @Valid @RequestBody CreateReviewRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<ReviewResponseDTO>();
        
        // Validate input
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        try {
            ReviewResponseDTO review = reviewRestService.createReview(dto);
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(review);
            baseResponseDTO.setMessage("Review successfully created with ID: " + review.getReviewID());
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
            
        } catch (AccessDeniedException e) {
            baseResponseDTO.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponseDTO.setMessage(e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.FORBIDDEN);
            
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Error: " + e.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
