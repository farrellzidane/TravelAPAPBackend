package apap.ti._5.accommodation_2306275600_be.restcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.review.CreateReviewRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.review.ReviewResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.ReviewRestService;

@ExtendWith(MockitoExtension.class)
class ReviewRestControllerTest {

    @Mock
    private ReviewRestService reviewRestService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ReviewRestController reviewRestController;

    private UUID testPropertyId;
    private UUID testCustomerId;
    private UUID testReviewId;
    private UUID testBookingId;
    private ReviewResponseDTO mockReview;
    private CreateReviewRequestDTO mockCreateReviewRequest;

    @BeforeEach
    void setUp() {
        testPropertyId = UUID.randomUUID();
        testCustomerId = UUID.randomUUID();
        testReviewId = UUID.randomUUID();
        testBookingId = UUID.randomUUID();

        // Setup mock review
        mockReview = ReviewResponseDTO.builder()
                .reviewID(testReviewId.toString())
                .bookingID(testBookingId)
                .customerID(testCustomerId.toString())
                .customerName("Test Customer")
                .propertyName("Test Property")
                .overallRating(4.5)
                .cleanlinessRating(5)
                .facilityRating(4)
                .serviceRating(5)
                .valueRating(4)
                .comment("Great stay!")
                .createdDate(LocalDateTime.now())
                .build();

        // Setup mock create request
        mockCreateReviewRequest = new CreateReviewRequestDTO();
        mockCreateReviewRequest.setBookingID(testBookingId.toString());
        mockCreateReviewRequest.setCleanlinessRating(5);
        mockCreateReviewRequest.setFacilityRating(4);
        mockCreateReviewRequest.setServiceRating(5);
        mockCreateReviewRequest.setValueRating(4);
        mockCreateReviewRequest.setComment("Great stay!");
    }

    // ========== GET REVIEWS BY PROPERTY TESTS ==========

    @Test
    void testGetReviewsByProperty_Success() {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(mockReview);
        when(reviewRestService.getReviewsByPropertyID(testPropertyId)).thenReturn(reviews);

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByProperty(testPropertyId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved"));
        assertTrue(response.getBody().getMessage().contains("1 review(s)"));
        verify(reviewRestService, times(1)).getReviewsByPropertyID(testPropertyId);
    }

    @Test
    void testGetReviewsByProperty_EmptyList() {
        // Arrange
        List<ReviewResponseDTO> reviews = new ArrayList<>();
        when(reviewRestService.getReviewsByPropertyID(testPropertyId)).thenReturn(reviews);

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByProperty(testPropertyId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("0 review(s)"));
    }

    @Test
    void testGetReviewsByProperty_MultipleReviews() {
        // Arrange
        ReviewResponseDTO review2 = ReviewResponseDTO.builder()
                .reviewID(UUID.randomUUID().toString())
                .bookingID(UUID.randomUUID())
                .customerID(UUID.randomUUID().toString())
                .customerName("Another Customer")
                .propertyName("Test Property")
                .overallRating(4.0)
                .build();
        
        List<ReviewResponseDTO> reviews = Arrays.asList(mockReview, review2);
        when(reviewRestService.getReviewsByPropertyID(testPropertyId)).thenReturn(reviews);

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByProperty(testPropertyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("2 review(s)"));
    }

    @Test
    void testGetReviewsByProperty_AccessDenied() {
        // Arrange
        when(reviewRestService.getReviewsByPropertyID(testPropertyId))
            .thenThrow(new AccessDeniedException("Access denied to property reviews"));

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByProperty(testPropertyId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Access denied"));
        assertNull(response.getBody().getData());
    }

    @Test
    void testGetReviewsByProperty_InternalServerError() {
        // Arrange
        when(reviewRestService.getReviewsByPropertyID(testPropertyId))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByProperty(testPropertyId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Error:"));
        assertTrue(response.getBody().getMessage().contains("Database error"));
    }

    // ========== GET REVIEWS BY CUSTOMER TESTS ==========

    @Test
    void testGetReviewsByCustomer_Success() {
        // Arrange
        List<ReviewResponseDTO> reviews = Arrays.asList(mockReview);
        when(reviewRestService.getReviewsByCustomerID(testCustomerId)).thenReturn(reviews);

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByCustomer(testCustomerId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved"));
        assertTrue(response.getBody().getMessage().contains("1 review(s)"));
        verify(reviewRestService, times(1)).getReviewsByCustomerID(testCustomerId);
    }

    @Test
    void testGetReviewsByCustomer_EmptyList() {
        // Arrange
        when(reviewRestService.getReviewsByCustomerID(testCustomerId)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByCustomer(testCustomerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("0 review(s)"));
    }

    @Test
    void testGetReviewsByCustomer_MultipleReviews() {
        // Arrange
        ReviewResponseDTO review2 = ReviewResponseDTO.builder()
                .reviewID(UUID.randomUUID().toString())
                .bookingID(UUID.randomUUID())
                .customerID(testCustomerId.toString())
                .customerName("Test Customer")
                .propertyName("Another Property")
                .overallRating(3.5)
                .build();
        
        List<ReviewResponseDTO> reviews = Arrays.asList(mockReview, review2);
        when(reviewRestService.getReviewsByCustomerID(testCustomerId)).thenReturn(reviews);

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByCustomer(testCustomerId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("2 review(s)"));
    }

    @Test
    void testGetReviewsByCustomer_AccessDenied() {
        // Arrange
        when(reviewRestService.getReviewsByCustomerID(testCustomerId))
            .thenThrow(new AccessDeniedException("Access denied to customer reviews"));

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByCustomer(testCustomerId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Access denied"));
    }

    @Test
    void testGetReviewsByCustomer_InternalServerError() {
        // Arrange
        when(reviewRestService.getReviewsByCustomerID(testCustomerId))
            .thenThrow(new RuntimeException("Service unavailable"));

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByCustomer(testCustomerId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error:"));
        assertTrue(response.getBody().getMessage().contains("Service unavailable"));
    }

    // ========== GET REVIEW DETAIL TESTS ==========

    @Test
    void testGetReviewDetail_Success() {
        // Arrange
        when(reviewRestService.getReviewByID(testReviewId)).thenReturn(mockReview);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.getReviewDetail(testReviewId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReview, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved review detail"));
        verify(reviewRestService, times(1)).getReviewByID(testReviewId);
    }

    @Test
    void testGetReviewDetail_AccessDenied() {
        // Arrange
        when(reviewRestService.getReviewByID(testReviewId))
            .thenThrow(new AccessDeniedException("Access denied to review detail"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.getReviewDetail(testReviewId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Access denied"));
        assertNull(response.getBody().getData());
    }

    @Test
    void testGetReviewDetail_InternalServerError() {
        // Arrange
        when(reviewRestService.getReviewByID(testReviewId))
            .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.getReviewDetail(testReviewId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error:"));
        assertTrue(response.getBody().getMessage().contains("Database connection failed"));
    }

    @Test
    void testGetReviewDetail_NullPointerException() {
        // Arrange
        when(reviewRestService.getReviewByID(testReviewId))
            .thenThrow(new NullPointerException("Review not found"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.getReviewDetail(testReviewId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error:"));
    }

    // ========== CREATE REVIEW TESTS ==========

    @Test
    void testCreateReview_Success() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class))).thenReturn(mockReview);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(HttpStatus.CREATED.value(), response.getBody().getStatus());
        assertEquals(mockReview, response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Review successfully created"));
        assertTrue(response.getBody().getMessage().contains(testReviewId.toString()));
        verify(reviewRestService, times(1)).createReview(any(CreateReviewRequestDTO.class));
    }

    @Test
    void testCreateReview_WithValidationErrors() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        FieldError error1 = new FieldError("createReviewRequestDTO", "cleanlinessRating", "Cleanliness rating is required");
        FieldError error2 = new FieldError("createReviewRequestDTO", "facilityRating", "Facility rating is required");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Cleanliness rating is required"));
        assertTrue(response.getBody().getMessage().contains("Facility rating is required"));
        assertNull(response.getBody().getData());
        verify(reviewRestService, never()).createReview(any());
    }

    @Test
    void testCreateReview_WithSingleValidationError() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        FieldError error = new FieldError("createReviewRequestDTO", "bookingID", "Booking ID is required");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Booking ID is required"));
    }

    @Test
    void testCreateReview_AccessDenied() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class)))
            .thenThrow(new AccessDeniedException("Customer cannot review this booking"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Customer cannot review this booking"));
        assertNull(response.getBody().getData());
    }

    @Test
    void testCreateReview_BadRequest() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Booking not found"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Error:"));
        assertTrue(response.getBody().getMessage().contains("Booking not found"));
    }

    @Test
    void testCreateReview_InvalidRating() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Rating must be between 1 and 5"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Rating must be between 1 and 5"));
    }

    @Test
    void testCreateReview_DuplicateReview() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class)))
            .thenThrow(new IllegalStateException("Review already exists for this booking"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Review already exists"));
    }

    @Test
    void testCreateReview_NullPointerException() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class)))
            .thenThrow(new NullPointerException("Required field is null"));

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error:"));
    }

    @Test
    void testCreateReview_WithComment() {
        // Arrange
        mockCreateReviewRequest.setComment("Excellent service and very clean rooms!");
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class))).thenReturn(mockReview);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockReview, response.getBody().getData());
    }

    @Test
    void testCreateReview_WithoutComment() {
        // Arrange
        mockCreateReviewRequest.setComment(null);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class))).thenReturn(mockReview);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockReview, response.getBody().getData());
    }

    @Test
    void testCreateReview_MinimumRatings() {
        // Arrange
        mockCreateReviewRequest.setCleanlinessRating(1);
        mockCreateReviewRequest.setFacilityRating(1);
        mockCreateReviewRequest.setServiceRating(1);
        mockCreateReviewRequest.setValueRating(1);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class))).thenReturn(mockReview);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateReview_MaximumRatings() {
        // Arrange
        mockCreateReviewRequest.setCleanlinessRating(5);
        mockCreateReviewRequest.setFacilityRating(5);
        mockCreateReviewRequest.setServiceRating(5);
        mockCreateReviewRequest.setValueRating(5);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(reviewRestService.createReview(any(CreateReviewRequestDTO.class))).thenReturn(mockReview);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // ========== ADDITIONAL EDGE CASE TESTS ==========

    @Test
    void testCreateReview_EmptyBindingErrors() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetReviewsByProperty_LargeNumberOfReviews() {
        // Arrange
        List<ReviewResponseDTO> reviews = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            reviews.add(ReviewResponseDTO.builder()
                    .reviewID(UUID.randomUUID().toString())
                    .bookingID(UUID.randomUUID())
                    .overallRating(4.0)
                    .build());
        }
        when(reviewRestService.getReviewsByPropertyID(testPropertyId)).thenReturn(reviews);

        // Act
        ResponseEntity<BaseResponseDTO<List<ReviewResponseDTO>>> response = 
            reviewRestController.getReviewsByProperty(testPropertyId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("100 review(s)"));
    }

    @Test
    void testCreateReview_MultipleValidationErrorsFormatting() {
        // Arrange
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        List<FieldError> errors = Arrays.asList(
            new FieldError("dto", "cleanlinessRating", "Cleanliness rating is required"),
            new FieldError("dto", "facilityRating", "Facility rating is required"),
            new FieldError("dto", "serviceRating", "Service rating is required"),
            new FieldError("dto", "valueRating", "Value rating is required")
        );
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<BaseResponseDTO<ReviewResponseDTO>> response = 
            reviewRestController.createReview(mockCreateReviewRequest, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody().getMessage();
        assertTrue(message.contains("Cleanliness rating is required"));
        assertTrue(message.contains("Facility rating is required"));
        assertTrue(message.contains("Service rating is required"));
        assertTrue(message.contains("Value rating is required"));
    }
}
