package apap.ti._5.accommodation_2306275600_be.restservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.model.AccommodationReview;
import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.AccommodationReviewRepository;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.review.CreateReviewRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.review.ReviewResponseDTO;

@ExtendWith(MockitoExtension.class)
class ReviewRestServiceImplTest {

    @Mock
    private AccommodationReviewRepository reviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReviewRestServiceImpl reviewRestService;

    private AccommodationReview testReview;
    private Booking testBooking;
    private Property testProperty;
    private Room testRoom;
    private RoomType testRoomType;
    private UUID reviewId;
    private UUID bookingId;
    private UUID propertyId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        testProperty = Property.builder()
            .propertyID(propertyId)
            .propertyName("Test Hotel")
            .type(1)
            .address("Test Address")
            .province(1)
            .description("Test Description")
            .totalRoom(10)
            .activeStatus(1)
            .income(0)
            .ownerID(UUID.randomUUID())
            .ownerName("Test Owner")
            .createdDate(LocalDateTime.now())
            .build();

        testRoomType = RoomType.builder()
            .roomTypeID(UUID.randomUUID())
            .name("Deluxe")
            .capacity(2)
            .price(500000)
            .facility("AC, TV")
            .floor(1)
            .property(testProperty)
            .build();

        testRoom = Room.builder()
            .roomID(UUID.randomUUID())
            .name("101")
            .availabilityStatus(1)
            .activeRoom(1)
            .roomType(testRoomType)
            .build();

        testBooking = Booking.builder()
            .bookingID(bookingId)
            .checkInDate(LocalDateTime.now().minusDays(5))
            .checkOutDate(LocalDateTime.now().minusDays(3))
            .totalDays(2)
            .totalPrice(1000000)
            .status(1) // Payment Confirmed
            .customerID(customerId)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .room(testRoom)
            .createdDate(LocalDateTime.now().minusDays(10))
            .build();

        testReview = new AccommodationReview();
        testReview.setReviewID(reviewId);
        testReview.setBooking(testBooking);
        testReview.setProperty(testProperty);
        testReview.setCustomerID(customerId);
        testReview.setOverallRating(4.5);
        testReview.setCleanlinessRating(5);
        testReview.setFacilityRating(4);
        testReview.setServiceRating(5);
        testReview.setValueRating(4);
        testReview.setComment("Great stay!");
        testReview.setCreatedDate(LocalDateTime.now());
    }

    // ============================================
    // GET REVIEWS BY PROPERTY ID TESTS
    // ============================================

    @Test
    void testGetReviewsByPropertyID_Success() throws AccessDeniedException {
        when(reviewRepository.findByPropertyID(propertyId))
            .thenReturn(Arrays.asList(testReview));

        List<ReviewResponseDTO> result = reviewRestService.getReviewsByPropertyID(propertyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewId.toString(), result.get(0).getReviewID());
        verify(reviewRepository).findByPropertyID(propertyId);
    }

    @Test
    void testGetReviewsByPropertyID_EmptyList() throws AccessDeniedException {
        when(reviewRepository.findByPropertyID(propertyId))
            .thenReturn(Collections.emptyList());

        List<ReviewResponseDTO> result = reviewRestService.getReviewsByPropertyID(propertyId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ============================================
    // GET REVIEWS BY CUSTOMER ID TESTS
    // ============================================

    @Test
    void testGetReviewsByCustomerID_Success() throws AccessDeniedException {
        when(reviewRepository.findByCustomerIDOrderByCreatedDateDesc(customerId))
            .thenReturn(Arrays.asList(testReview));

        List<ReviewResponseDTO> result = reviewRestService.getReviewsByCustomerID(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerId.toString(), result.get(0).getCustomerID());
        verify(reviewRepository).findByCustomerIDOrderByCreatedDateDesc(customerId);
    }

    @Test
    void testGetReviewsByCustomerID_EmptyList() throws AccessDeniedException {
        when(reviewRepository.findByCustomerIDOrderByCreatedDateDesc(customerId))
            .thenReturn(Collections.emptyList());

        List<ReviewResponseDTO> result = reviewRestService.getReviewsByCustomerID(customerId);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ============================================
    // GET REVIEW BY ID TESTS
    // ============================================

    @Test
    void testGetReviewByID_Success() throws AccessDeniedException {
        when(reviewRepository.findByReviewID(reviewId))
            .thenReturn(Optional.of(testReview));

        ReviewResponseDTO result = reviewRestService.getReviewByID(reviewId);

        assertNotNull(result);
        assertEquals(reviewId.toString(), result.getReviewID());
        assertEquals("Great stay!", result.getComment());
        verify(reviewRepository).findByReviewID(reviewId);
    }

    @Test
    void testGetReviewByID_NotFound_ThrowsException() {
        when(reviewRepository.findByReviewID(reviewId))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewRestService.getReviewByID(reviewId));
        
        assertTrue(exception.getMessage().contains("Review not found"));
    }

    // ============================================
    // CREATE REVIEW TESTS
    // ============================================

    @Test
    void testCreateReview_Success() throws AccessDeniedException {
        CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
        requestDTO.setBookingID(bookingId.toString());
        requestDTO.setCleanlinessRating(5);
        requestDTO.setFacilityRating(4);
        requestDTO.setServiceRating(5);
        requestDTO.setValueRating(4);
        requestDTO.setComment("Great stay!");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(reviewRepository.existsByBooking_BookingID(bookingId)).thenReturn(false);
        when(reviewRepository.save(any(AccommodationReview.class))).thenReturn(testReview);

        ReviewResponseDTO result = reviewRestService.createReview(requestDTO);

        assertNotNull(result);
        assertEquals(reviewId.toString(), result.getReviewID());
        assertEquals("Great stay!", result.getComment());
        verify(bookingRepository).findById(bookingId);
        verify(reviewRepository).existsByBooking_BookingID(bookingId);
        verify(reviewRepository).save(any(AccommodationReview.class));
    }

    @Test
    void testCreateReview_BookingNotFound_ThrowsException() {
        CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
        requestDTO.setBookingID(bookingId.toString());
        requestDTO.setCleanlinessRating(5);
        requestDTO.setFacilityRating(4);
        requestDTO.setServiceRating(5);
        requestDTO.setValueRating(4);
        requestDTO.setComment("Great stay!");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewRestService.createReview(requestDTO));
        
        assertTrue(exception.getMessage().contains("Booking not found"));
        verify(reviewRepository, never()).save(any(AccommodationReview.class));
    }

    @Test
    void testCreateReview_BookingStatusNotConfirmed_ThrowsException() {
        testBooking.setStatus(0); // Waiting for Payment

        CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
        requestDTO.setBookingID(bookingId.toString());
        requestDTO.setCleanlinessRating(5);
        requestDTO.setFacilityRating(4);
        requestDTO.setServiceRating(5);
        requestDTO.setValueRating(4);
        requestDTO.setComment("Great stay!");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewRestService.createReview(requestDTO));
        
        assertTrue(exception.getMessage().contains("Payment Confirmed status"));
        verify(reviewRepository, never()).save(any(AccommodationReview.class));
    }

    @Test
    void testCreateReview_BeforeCheckout_ThrowsException() {
        testBooking.setCheckOutDate(LocalDateTime.now().plusDays(1));

        CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
        requestDTO.setBookingID(bookingId.toString());
        requestDTO.setCleanlinessRating(5);
        requestDTO.setFacilityRating(4);
        requestDTO.setServiceRating(5);
        requestDTO.setValueRating(4);
        requestDTO.setComment("Great stay!");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewRestService.createReview(requestDTO));
        
        assertTrue(exception.getMessage().contains("after checkout date has passed"));
        verify(reviewRepository, never()).save(any(AccommodationReview.class));
    }

    @Test
    void testCreateReview_ReviewAlreadyExists_ThrowsException() {
        CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
        requestDTO.setBookingID(bookingId.toString());
        requestDTO.setCleanlinessRating(5);
        requestDTO.setFacilityRating(4);
        requestDTO.setServiceRating(5);
        requestDTO.setValueRating(4);
        requestDTO.setComment("Great stay!");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(reviewRepository.existsByBooking_BookingID(bookingId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewRestService.createReview(requestDTO));
        
        assertTrue(exception.getMessage().contains("Review already exists"));
        verify(reviewRepository, never()).save(any(AccommodationReview.class));
    }

    @Test
    void testCreateReview_WithAllRatings() throws AccessDeniedException {
        CreateReviewRequestDTO requestDTO = new CreateReviewRequestDTO();
        requestDTO.setBookingID(bookingId.toString());
        requestDTO.setCleanlinessRating(4);
        requestDTO.setFacilityRating(3);
        requestDTO.setServiceRating(5);
        requestDTO.setValueRating(4);
        requestDTO.setComment("Nice hotel with excellent service");

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(reviewRepository.existsByBooking_BookingID(bookingId)).thenReturn(false);
        when(reviewRepository.save(any(AccommodationReview.class))).thenReturn(testReview);

        ReviewResponseDTO result = reviewRestService.createReview(requestDTO);

        assertNotNull(result);
        verify(reviewRepository).save(argThat(review -> 
            review.getCleanlinessRating() == 4 &&
            review.getFacilityRating() == 3 &&
            review.getServiceRating() == 5 &&
            review.getValueRating() == 4
        ));
    }
}
