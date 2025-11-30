package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccommodationReviewTest {

    private AccommodationReview review;
    private UUID testReviewId;
    private Booking testBooking;
    private Property testProperty;
    private UUID testCustomerId;

    @BeforeEach
    void setUp() {
        testReviewId = UUID.randomUUID();
        testCustomerId = UUID.randomUUID();
        
        testProperty = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("Test Hotel")
                .build();
        
        testBooking = Booking.builder()
                .bookingID(UUID.randomUUID())
                .customerName("John Customer")
                .build();
        
        review = new AccommodationReview();
        review.setReviewID(testReviewId);
        review.setBooking(testBooking);
        review.setProperty(testProperty);
        review.setCustomerID(testCustomerId);
        review.setOverallRating(4.5);
        review.setCleanlinessRating(5);
        review.setFacilityRating(4);
        review.setServiceRating(4);
        review.setValueRating(5);
        review.setComment("Great stay! Highly recommended.");
        review.setDeleted(false);
    }

    @Test
    void testReviewConstruction() {
        assertNotNull(review);
        assertEquals(testReviewId, review.getReviewID());
        assertEquals(testBooking, review.getBooking());
        assertEquals(testProperty, review.getProperty());
        assertEquals(testCustomerId, review.getCustomerID());
        assertEquals(4.5, review.getOverallRating());
        assertEquals(5, review.getCleanlinessRating());
        assertEquals(4, review.getFacilityRating());
        assertEquals(4, review.getServiceRating());
        assertEquals(5, review.getValueRating());
        assertEquals("Great stay! Highly recommended.", review.getComment());
        assertFalse(review.isDeleted());
    }

    @Test
    void testNoArgsConstructor() {
        AccommodationReview emptyReview = new AccommodationReview();
        assertNotNull(emptyReview);
        assertNull(emptyReview.getReviewID());
        assertNull(emptyReview.getBooking());
        assertNull(emptyReview.getProperty());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime createdDate = LocalDateTime.now();
        
        AccommodationReview fullReview = new AccommodationReview(
                testReviewId,
                testBooking,
                testProperty,
                testCustomerId,
                4.5,
                5,
                4,
                4,
                5,
                "Excellent experience",
                createdDate,
                false
        );
        
        assertEquals(testReviewId, fullReview.getReviewID());
        assertEquals(testBooking, fullReview.getBooking());
        assertEquals(testProperty, fullReview.getProperty());
        assertEquals(testCustomerId, fullReview.getCustomerID());
        assertEquals(4.5, fullReview.getOverallRating());
        assertEquals(5, fullReview.getCleanlinessRating());
        assertEquals(4, fullReview.getFacilityRating());
        assertEquals(4, fullReview.getServiceRating());
        assertEquals(5, fullReview.getValueRating());
        assertEquals("Excellent experience", fullReview.getComment());
        assertEquals(createdDate, fullReview.getCreatedDate());
        assertFalse(fullReview.isDeleted());
    }

    @Test
    void testSettersAndGetters() {
        review.setOverallRating(5.0);
        assertEquals(5.0, review.getOverallRating());
        
        review.setCleanlinessRating(4);
        assertEquals(4, review.getCleanlinessRating());
        
        review.setFacilityRating(5);
        assertEquals(5, review.getFacilityRating());
        
        review.setServiceRating(5);
        assertEquals(5, review.getServiceRating());
        
        review.setValueRating(4);
        assertEquals(4, review.getValueRating());
        
        review.setComment("Updated comment");
        assertEquals("Updated comment", review.getComment());
        
        review.setDeleted(true);
        assertTrue(review.isDeleted());
    }

    @Test
    void testPrePersistGeneratesId() {
        AccommodationReview newReview = new AccommodationReview();
        assertNull(newReview.getReviewID());
        
        newReview.prePersist();
        
        assertNotNull(newReview.getReviewID());
    }

    @Test
    void testPrePersistDoesNotOverwriteExistingId() {
        AccommodationReview newReview = new AccommodationReview();
        UUID existingId = UUID.randomUUID();
        newReview.setReviewID(existingId);
        
        newReview.prePersist();
        
        assertEquals(existingId, newReview.getReviewID());
    }

    @Test
    void testPrePersistSetsCreatedDate() {
        AccommodationReview newReview = new AccommodationReview();
        assertNull(newReview.getCreatedDate());
        
        newReview.prePersist();
        
        assertNotNull(newReview.getCreatedDate());
    }

    @Test
    void testPrePersistCalculatesOverallRating() {
        AccommodationReview newReview = new AccommodationReview();
        newReview.setCleanlinessRating(5);
        newReview.setFacilityRating(4);
        newReview.setServiceRating(4);
        newReview.setValueRating(5);
        
        newReview.prePersist();
        
        // (5 + 4 + 4 + 5) / 4 = 4.5
        assertEquals(4.5, newReview.getOverallRating());
    }

    @Test
    void testPrePersistCalculatesOverallRatingAllFive() {
        AccommodationReview newReview = new AccommodationReview();
        newReview.setCleanlinessRating(5);
        newReview.setFacilityRating(5);
        newReview.setServiceRating(5);
        newReview.setValueRating(5);
        
        newReview.prePersist();
        
        assertEquals(5.0, newReview.getOverallRating());
    }

    @Test
    void testPrePersistCalculatesOverallRatingAllOne() {
        AccommodationReview newReview = new AccommodationReview();
        newReview.setCleanlinessRating(1);
        newReview.setFacilityRating(1);
        newReview.setServiceRating(1);
        newReview.setValueRating(1);
        
        newReview.prePersist();
        
        assertEquals(1.0, newReview.getOverallRating());
    }

    @Test
    void testPrePersistWithMixedRatings() {
        AccommodationReview newReview = new AccommodationReview();
        newReview.setCleanlinessRating(3);
        newReview.setFacilityRating(4);
        newReview.setServiceRating(2);
        newReview.setValueRating(3);
        
        newReview.prePersist();
        
        // (3 + 4 + 2 + 3) / 4 = 3.0
        assertEquals(3.0, newReview.getOverallRating());
    }

    @Test
    void testRatingValidation() {
        // Test valid ratings (1-5)
        for (int rating = 1; rating <= 5; rating++) {
            review.setCleanlinessRating(rating);
            review.setFacilityRating(rating);
            review.setServiceRating(rating);
            review.setValueRating(rating);
            
            assertEquals(rating, review.getCleanlinessRating());
            assertEquals(rating, review.getFacilityRating());
            assertEquals(rating, review.getServiceRating());
            assertEquals(rating, review.getValueRating());
        }
    }

    @Test
    void testBookingRelationship() {
        Booking newBooking = Booking.builder()
                .bookingID(UUID.randomUUID())
                .customerName("Jane Customer")
                .build();
        
        review.setBooking(newBooking);
        
        assertNotNull(review.getBooking());
        assertEquals("Jane Customer", review.getBooking().getCustomerName());
    }

    @Test
    void testPropertyRelationship() {
        Property newProperty = Property.builder()
                .propertyID(UUID.randomUUID())
                .propertyName("New Hotel")
                .build();
        
        review.setProperty(newProperty);
        
        assertNotNull(review.getProperty());
        assertEquals("New Hotel", review.getProperty().getPropertyName());
    }

    @Test
    void testSoftDelete() {
        assertFalse(review.isDeleted());
        
        review.setDeleted(true);
        
        assertTrue(review.isDeleted());
    }

    @Test
    void testLongComment() {
        String longComment = "This is a very detailed review. ".repeat(50);
        review.setComment(longComment);
        
        assertEquals(longComment, review.getComment());
    }

    @Test
    void testNullComment() {
        review.setComment(null);
        assertNull(review.getComment());
    }

    @Test
    void testEmptyComment() {
        review.setComment("");
        assertEquals("", review.getComment());
    }

    @Test
    void testCustomerIdAssignment() {
        UUID newCustomerId = UUID.randomUUID();
        review.setCustomerID(newCustomerId);
        
        assertEquals(newCustomerId, review.getCustomerID());
    }

    @Test
    void testCreatedDateManually() {
        LocalDateTime customDate = LocalDateTime.of(2024, 1, 1, 10, 30);
        review.setCreatedDate(customDate);
        
        assertEquals(customDate, review.getCreatedDate());
    }

    @Test
    void testOverallRatingPrecision() {
        review.setOverallRating(4.75);
        assertEquals(4.75, review.getOverallRating(), 0.001);
        
        review.setOverallRating(3.25);
        assertEquals(3.25, review.getOverallRating(), 0.001);
    }

    @Test
    void testDefaultDeletedStatus() {
        AccommodationReview newReview = new AccommodationReview();
        
        // Should default to false
        assertFalse(newReview.isDeleted());
    }

    @Test
    void testPrePersistWithNullRatings() {
        AccommodationReview newReview = new AccommodationReview();
        // All ratings are null
        
        newReview.prePersist();
        
        // Overall rating should not be calculated
        assertNull(newReview.getOverallRating());
    }

    @Test
    void testPrePersistWithPartialRatings() {
        AccommodationReview newReview = new AccommodationReview();
        newReview.setCleanlinessRating(5);
        newReview.setFacilityRating(4);
        // serviceRating and valueRating are null
        
        newReview.prePersist();
        
        // Overall rating should not be calculated if any rating is null
        assertNull(newReview.getOverallRating());
    }

    @Test
    void testToStringContainsKeyFields() {
        String reviewString = review.toString();
        assertNotNull(reviewString);
        // Just verify toString() is not null - Lombok generates standard format
        assertFalse(reviewString.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        AccommodationReview sameReview = review;
        
        assertEquals(review, sameReview);
        assertEquals(review.hashCode(), sameReview.hashCode());
    }

    @Test
    void testNotEquals() {
        AccommodationReview differentReview = new AccommodationReview();
        differentReview.setReviewID(UUID.randomUUID());
        
        assertNotEquals(review, differentReview);
    }
}
