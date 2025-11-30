package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;
    private UUID testBookingId;
    private UUID testCustomerId;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testBookingId = UUID.randomUUID();
        testCustomerId = UUID.randomUUID();
        
        testRoom = Room.builder()
                .roomID(UUID.randomUUID())
                .name("Room 101")
                .availabilityStatus(1)
                .activeRoom(1)
                .build();
        
        booking = Booking.builder()
                .bookingID(testBookingId)
                .checkInDate(LocalDateTime.now().plusDays(1))
                .checkOutDate(LocalDateTime.now().plusDays(3))
                .totalDays(2)
                .totalPrice(1000000)
                .status(0)
                .customerID(testCustomerId)
                .customerName("John Customer")
                .customerEmail("john@example.com")
                .customerPhone("08123456789")
                .isBreakfast(true)
                .capacity(2)
                .room(testRoom)
                .build();
    }

    @Test
    void testBookingBuilder() {
        assertNotNull(booking);
        assertEquals(testBookingId, booking.getBookingID());
        assertNotNull(booking.getCheckInDate());
        assertNotNull(booking.getCheckOutDate());
        assertEquals(2, booking.getTotalDays());
        assertEquals(1000000, booking.getTotalPrice());
        assertEquals(0, booking.getStatus());
        assertEquals(testCustomerId, booking.getCustomerID());
        assertEquals("John Customer", booking.getCustomerName());
        assertEquals("john@example.com", booking.getCustomerEmail());
        assertEquals("08123456789", booking.getCustomerPhone());
        assertTrue(booking.isBreakfast());
        assertEquals(2, booking.getCapacity());
        assertEquals(testRoom, booking.getRoom());
    }

    @Test
    void testNoArgsConstructor() {
        Booking emptyBooking = new Booking();
        assertNotNull(emptyBooking);
        assertNull(emptyBooking.getBookingID());
        assertNull(emptyBooking.getCustomerName());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        
        Booking fullBooking = new Booking(
                testBookingId,
                checkIn,
                checkOut,
                2,
                1500000,
                1,
                testCustomerId,
                "Jane Customer",
                "jane@example.com",
                "08987654321",
                false,
                3,
                created,
                updated,
                testRoom,
                null
        );
        
        assertEquals(testBookingId, fullBooking.getBookingID());
        assertEquals(checkIn, fullBooking.getCheckInDate());
        assertEquals(checkOut, fullBooking.getCheckOutDate());
        assertEquals(2, fullBooking.getTotalDays());
        assertEquals(1500000, fullBooking.getTotalPrice());
        assertEquals(1, fullBooking.getStatus());
        assertEquals(testCustomerId, fullBooking.getCustomerID());
        assertEquals("Jane Customer", fullBooking.getCustomerName());
        assertEquals("jane@example.com", fullBooking.getCustomerEmail());
        assertEquals("08987654321", fullBooking.getCustomerPhone());
        assertFalse(fullBooking.isBreakfast());
        assertEquals(3, fullBooking.getCapacity());
        assertEquals(testRoom, fullBooking.getRoom());
    }

    @Test
    void testSettersAndGetters() {
        LocalDateTime newCheckIn = LocalDateTime.now().plusDays(5);
        LocalDateTime newCheckOut = LocalDateTime.now().plusDays(7);
        UUID newCustomerId = UUID.randomUUID();
        
        booking.setCheckInDate(newCheckIn);
        assertEquals(newCheckIn, booking.getCheckInDate());
        
        booking.setCheckOutDate(newCheckOut);
        assertEquals(newCheckOut, booking.getCheckOutDate());
        
        booking.setTotalDays(3);
        assertEquals(3, booking.getTotalDays());
        
        booking.setTotalPrice(1500000);
        assertEquals(1500000, booking.getTotalPrice());
        
        booking.setStatus(2);
        assertEquals(2, booking.getStatus());
        
        booking.setCustomerID(newCustomerId);
        assertEquals(newCustomerId, booking.getCustomerID());
        
        booking.setCustomerName("Updated Name");
        assertEquals("Updated Name", booking.getCustomerName());
        
        booking.setCustomerEmail("updated@example.com");
        assertEquals("updated@example.com", booking.getCustomerEmail());
        
        booking.setCustomerPhone("08111111111");
        assertEquals("08111111111", booking.getCustomerPhone());
        
        booking.setBreakfast(false);
        assertFalse(booking.isBreakfast());
        
        booking.setCapacity(4);
        assertEquals(4, booking.getCapacity());
    }

    @Test
    void testRoomRelationship() {
        Room newRoom = Room.builder()
                .roomID(UUID.randomUUID())
                .name("Room 202")
                .build();
        
        booking.setRoom(newRoom);
        
        assertNotNull(booking.getRoom());
        assertEquals("Room 202", booking.getRoom().getName());
    }

    @Test
    void testReviewRelationship() {
        AccommodationReview review = new AccommodationReview();
        review.setReviewID(UUID.randomUUID());
        review.setOverallRating(4.5);
        
        booking.setReview(review);
        
        assertNotNull(booking.getReview());
        assertEquals(4.5, booking.getReview().getOverallRating());
    }

    @Test
    void testOnCreate() {
        Booking newBooking = new Booking();
        assertNull(newBooking.getBookingID());
        assertNull(newBooking.getCreatedDate());
        assertNull(newBooking.getUpdatedDate());
        
        // Simulate @PrePersist
        newBooking.onCreate();
        
        assertNotNull(newBooking.getBookingID());
        assertNotNull(newBooking.getCreatedDate());
        assertNotNull(newBooking.getUpdatedDate());
        assertEquals(newBooking.getCreatedDate(), newBooking.getUpdatedDate());
    }

    @Test
    void testOnCreateWithExistingId() {
        Booking newBooking = new Booking();
        UUID existingId = UUID.randomUUID();
        newBooking.setBookingID(existingId);
        
        newBooking.onCreate();
        
        // Should not change existing ID
        assertEquals(existingId, newBooking.getBookingID());
        assertNotNull(newBooking.getCreatedDate());
    }

    @Test
    void testOnUpdate() {
        booking.onCreate();
        LocalDateTime originalCreatedDate = booking.getCreatedDate();
        LocalDateTime originalUpdatedDate = booking.getUpdatedDate();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        booking.onUpdate();
        
        assertEquals(originalCreatedDate, booking.getCreatedDate());
        assertNotEquals(originalUpdatedDate, booking.getUpdatedDate());
        assertTrue(booking.getUpdatedDate().isAfter(originalUpdatedDate));
    }

    @Test
    void testBookingStatuses() {
        // Test different booking statuses (0-4 per specification)
        for (int status = 0; status <= 4; status++) {
            booking.setStatus(status);
            assertEquals(status, booking.getStatus());
        }
    }

    @Test
    void testEqualsAndHashCode() {
        Booking sameBooking = Booking.builder()
                .bookingID(testBookingId)
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalDays(2)
                .totalPrice(1000000)
                .status(0)
                .customerID(testCustomerId)
                .customerName("John Customer")
                .customerEmail("john@example.com")
                .customerPhone("08123456789")
                .isBreakfast(true)
                .capacity(2)
                .room(testRoom)
                .build();
        
        assertEquals(booking, sameBooking);
        assertEquals(booking.hashCode(), sameBooking.hashCode());
    }

    @Test
    void testNotEquals() {
        Booking differentBooking = Booking.builder()
                .bookingID(UUID.randomUUID())
                .customerName("Different Customer")
                .build();
        
        assertNotEquals(booking, differentBooking);
    }

    @Test
    void testToString() {
        String bookingString = booking.toString();
        assertNotNull(bookingString);
        assertTrue(bookingString.contains("John Customer"));
        assertTrue(bookingString.contains(testBookingId.toString()));
    }

    @Test
    void testNullValues() {
        Booking nullBooking = Booking.builder().build();
        
        assertNull(nullBooking.getBookingID());
        assertNull(nullBooking.getCheckInDate());
        assertNull(nullBooking.getCheckOutDate());
        assertNull(nullBooking.getCustomerName());
        assertNull(nullBooking.getCustomerEmail());
        assertNull(nullBooking.getCustomerPhone());
        assertNull(nullBooking.getRoom());
    }

    @Test
    void testDateTimeValidation() {
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);
        
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        
        assertTrue(booking.getCheckOutDate().isAfter(booking.getCheckInDate()));
    }

    @Test
    void testBreakfastFlag() {
        booking.setBreakfast(true);
        assertTrue(booking.isBreakfast());
        
        booking.setBreakfast(false);
        assertFalse(booking.isBreakfast());
    }

    @Test
    void testCapacityRange() {
        // Test various capacity values
        int[] capacities = {1, 2, 4, 6, 8};
        for (int capacity : capacities) {
            booking.setCapacity(capacity);
            assertEquals(capacity, booking.getCapacity());
        }
    }

    @Test
    void testTotalPriceCalculation() {
        booking.setTotalDays(3);
        int pricePerNight = 500000;
        int expectedTotal = 3 * pricePerNight;
        
        booking.setTotalPrice(expectedTotal);
        assertEquals(expectedTotal, booking.getTotalPrice());
    }

    @Test
    void testDateTimeFields() {
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedDate = LocalDateTime.now();
        
        booking.setCreatedDate(createdDate);
        booking.setUpdatedDate(updatedDate);
        
        assertEquals(createdDate, booking.getCreatedDate());
        assertEquals(updatedDate, booking.getUpdatedDate());
        assertTrue(booking.getUpdatedDate().isAfter(booking.getCreatedDate()));
    }

    @Test
    void testCustomerInformation() {
        booking.setCustomerName("Alice Customer");
        booking.setCustomerEmail("alice@example.com");
        booking.setCustomerPhone("08555555555");
        
        assertEquals("Alice Customer", booking.getCustomerName());
        assertEquals("alice@example.com", booking.getCustomerEmail());
        assertEquals("08555555555", booking.getCustomerPhone());
    }
}
