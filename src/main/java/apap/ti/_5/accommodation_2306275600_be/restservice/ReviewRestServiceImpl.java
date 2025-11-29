package apap.ti._5.accommodation_2306275600_be.restservice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.model.AccommodationReview;
import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.repository.AccommodationReviewRepository;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.review.CreateReviewRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.review.ReviewResponseDTO;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewRestServiceImpl implements ReviewRestService {
    
    @Autowired
    private AccommodationReviewRepository reviewRepository;
    
    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public List<ReviewResponseDTO> getReviewsByPropertyID(UUID propertyID) throws AccessDeniedException {
        List<AccommodationReview> reviews = reviewRepository.findByPropertyID(propertyID);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByCustomerID(UUID customerID) throws AccessDeniedException {
        List<AccommodationReview> reviews = reviewRepository.findByCustomerIDOrderByCreatedDateDesc(customerID);
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDTO getReviewByID(UUID reviewID) throws AccessDeniedException {
        AccommodationReview review = reviewRepository.findByReviewID(reviewID)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewID));
        return convertToResponseDTO(review);
    }

    @Override
    public ReviewResponseDTO createReview(CreateReviewRequestDTO dto) throws AccessDeniedException {
        // Validate booking exists
        UUID bookingID = UUID.fromString(dto.getBookingID());
        Booking booking = bookingRepository.findById(bookingID)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + dto.getBookingID()));
        
        // Validate booking status is Payment Confirmed (1)
        if (booking.getStatus() != 1) {
            throw new RuntimeException("Can only review bookings with Payment Confirmed status");
        }
        
        // Validate checkout date has passed
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(booking.getCheckOutDate())) {
            throw new RuntimeException("Can only review after checkout date has passed");
        }
        
        // Check if review already exists for this booking
        if (reviewRepository.existsByBooking_BookingID(bookingID)) {
            throw new RuntimeException("Review already exists for this booking");
        }
        
        // Create new review
        AccommodationReview review = new AccommodationReview();
        review.setBooking(booking);
        review.setProperty(booking.getRoom().getRoomType().getProperty());
        review.setCustomerID(booking.getCustomerID());
        review.setCleanlinessRating(dto.getCleanlinessRating());
        review.setFacilityRating(dto.getFacilityRating());
        review.setServiceRating(dto.getServiceRating());
        review.setValueRating(dto.getValueRating());
        review.setComment(dto.getComment());
        
        // Save review (prePersist will calculate overall rating and set dates)
        AccommodationReview savedReview = reviewRepository.save(review);
        
        return convertToResponseDTO(savedReview);
    }
    
    private ReviewResponseDTO convertToResponseDTO(AccommodationReview review) {
        return ReviewResponseDTO.builder()
                .reviewID(review.getReviewID().toString())
                .bookingID(review.getBooking().getBookingID())
                .customerID(review.getCustomerID().toString())
                .customerName(review.getBooking().getCustomerName())
                .propertyName(review.getBooking().getRoom().getRoomType().getProperty().getPropertyName())
                .overallRating(review.getOverallRating())
                .cleanlinessRating(review.getCleanlinessRating())
                .facilityRating(review.getFacilityRating())
                .serviceRating(review.getServiceRating())
                .valueRating(review.getValueRating())
                .comment(review.getComment())
                .createdDate(review.getCreatedDate())
                .build();
    }
}
