package apap.ti._5.accommodation_2306275600_be.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import apap.ti._5.accommodation_2306275600_be.model.AccommodationReview;

@Repository
public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, UUID> {
    
    // Find all reviews by property ID
    @Query("SELECT ar FROM AccommodationReview ar " +
           "JOIN ar.booking b " +
           "JOIN b.room r " +
           "JOIN r.roomType rt " +
           "JOIN rt.property p " +
           "WHERE p.propertyID = :propertyID " +
           "ORDER BY ar.createdDate DESC")
    List<AccommodationReview> findByPropertyID(@Param("propertyID") UUID propertyID);
    
    // Find all reviews by customer ID
    List<AccommodationReview> findByCustomerIDOrderByCreatedDateDesc(UUID customerID);
    
    // Find review by ID
    Optional<AccommodationReview> findByReviewID(UUID reviewID);
    
    // Check if review already exists for a booking
    boolean existsByBooking_BookingID(UUID bookingID);
    
    // Find review by booking ID
    Optional<AccommodationReview> findByBooking_BookingID(UUID bookingID);
}
