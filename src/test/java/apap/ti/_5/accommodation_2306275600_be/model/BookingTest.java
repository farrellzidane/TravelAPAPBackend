package apap.ti._5.accommodation_2306275600_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

class BookingTest {

    private Booking booking;
    private Room room;
    private RoomType roomType;
    private Property property;

    @BeforeEach
    void setUp() {
        // Setup Property
        property = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Test Hotel")
                .type(1)
                .address("Test Address")
                .province(1)
                .description("Test Description")
                .totalRoom(10)
                .activeStatus(1)
                .income(0)
                .ownerName("Test Owner")
                .ownerID(UUID.randomUUID())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Setup RoomType
        roomType = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe Room")
                .price(500000)
                .description("Deluxe room with ocean view")
                .capacity(2)
                .facility("AC, TV, WiFi")
                .floor(2)
                .property(property)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Setup Room
        room = Room.builder()
                .roomID("ROOM-001")
                .name("Room 101")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(roomType)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        // Setup Booking
        booking = Booking.builder()
                .bookingID("BOOK-001")
                .checkInDate(LocalDateTime.now().plusDays(1))
                .checkOutDate(LocalDateTime.now().plusDays(3))
                .totalDays(2)
                .totalPrice(1000000)
                .status(0)
                .customerID(UUID.randomUUID())
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .customerPhone("081234567890")
                .isBreakfast(true)
                .refund(0)
                .extraPay(0)
                .capacity(2)
                .room(room)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testBookingCreation() {
        assertNotNull(booking);
        assertEquals("BOOK-001", booking.getBookingID());
        assertEquals("John Doe", booking.getCustomerName());
        assertEquals("john@example.com", booking.getCustomerEmail());
        assertEquals("081234567890", booking.getCustomerPhone());
        assertEquals(2, booking.getTotalDays());
        assertEquals(1000000, booking.getTotalPrice());
        assertEquals(0, booking.getStatus());
        assertTrue(booking.isBreakfast());
        assertEquals(2, booking.getCapacity());
        assertNotNull(booking.getRoom());
    }

    @Test
    void testBookingBuilder() {
        UUID customerId = UUID.randomUUID();
        LocalDateTime checkIn = LocalDateTime.of(2025, 11, 10, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2025, 11, 12, 12, 0);

        Booking newBooking = Booking.builder()
                .bookingID("BOOK-002")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .totalDays(2)
                .totalPrice(1500000)
                .status(1)
                .customerID(customerId)
                .customerName("Jane Smith")
                .customerEmail("jane@example.com")
                .customerPhone("082345678901")
                .isBreakfast(false)
                .refund(0)
                .extraPay(100000)
                .capacity(3)
                .room(room)
                .build();

        assertEquals("BOOK-002", newBooking.getBookingID());
        assertEquals(checkIn, newBooking.getCheckInDate());
        assertEquals(checkOut, newBooking.getCheckOutDate());
        assertEquals(2, newBooking.getTotalDays());
        assertEquals(1500000, newBooking.getTotalPrice());
        assertEquals(1, newBooking.getStatus());
        assertEquals(customerId, newBooking.getCustomerID());
        assertEquals("Jane Smith", newBooking.getCustomerName());
        assertEquals("jane@example.com", newBooking.getCustomerEmail());
        assertEquals("082345678901", newBooking.getCustomerPhone());
        assertFalse(newBooking.isBreakfast());
        assertEquals(100000, newBooking.getExtraPay());
        assertEquals(3, newBooking.getCapacity());
    }

    @Test
    void testBookingSettersAndGetters() {
        booking.setBookingID("BOOK-UPDATE");
        assertEquals("BOOK-UPDATE", booking.getBookingID());

        LocalDateTime newCheckIn = LocalDateTime.now().plusDays(5);
        booking.setCheckInDate(newCheckIn);
        assertEquals(newCheckIn, booking.getCheckInDate());

        LocalDateTime newCheckOut = LocalDateTime.now().plusDays(7);
        booking.setCheckOutDate(newCheckOut);
        assertEquals(newCheckOut, booking.getCheckOutDate());

        booking.setTotalDays(5);
        assertEquals(5, booking.getTotalDays());

        booking.setTotalPrice(2500000);
        assertEquals(2500000, booking.getTotalPrice());

        booking.setStatus(2);
        assertEquals(2, booking.getStatus());

        UUID newCustomerId = UUID.randomUUID();
        booking.setCustomerID(newCustomerId);
        assertEquals(newCustomerId, booking.getCustomerID());

        booking.setCustomerName("Updated Name");
        assertEquals("Updated Name", booking.getCustomerName());

        booking.setCustomerEmail("updated@example.com");
        assertEquals("updated@example.com", booking.getCustomerEmail());

        booking.setCustomerPhone("083456789012");
        assertEquals("083456789012", booking.getCustomerPhone());

        booking.setBreakfast(false);
        assertFalse(booking.isBreakfast());

        booking.setRefund(500000);
        assertEquals(500000, booking.getRefund());

        booking.setExtraPay(200000);
        assertEquals(200000, booking.getExtraPay());

        booking.setCapacity(4);
        assertEquals(4, booking.getCapacity());

        Room newRoom = Room.builder().roomID("ROOM-002").build();
        booking.setRoom(newRoom);
        assertEquals("ROOM-002", booking.getRoom().getRoomID());
    }

    @Test
    void testBookingNoArgsConstructor() {
        Booking emptyBooking = new Booking();
        assertNotNull(emptyBooking);
        assertNull(emptyBooking.getBookingID());
        assertNull(emptyBooking.getCustomerName());
        assertEquals(0, emptyBooking.getTotalPrice());
    }

    @Test
    void testBookingAllArgsConstructor() {
        UUID customerId = UUID.randomUUID();
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = LocalDateTime.now().plusDays(2);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        Booking newBooking = new Booking(
            "BOOK-003",
            checkIn,
            checkOut,
            2,
            1000000,
            1,
            customerId,
            "Test Customer",
            "test@example.com",
            "081111111111",
            true,
            0,
            0,
            2,
            created,
            updated,
            room
        );

        assertEquals("BOOK-003", newBooking.getBookingID());
        assertEquals(checkIn, newBooking.getCheckInDate());
        assertEquals(checkOut, newBooking.getCheckOutDate());
        assertEquals(2, newBooking.getTotalDays());
        assertEquals(1000000, newBooking.getTotalPrice());
        assertEquals(1, newBooking.getStatus());
        assertEquals(customerId, newBooking.getCustomerID());
        assertEquals("Test Customer", newBooking.getCustomerName());
        assertEquals("test@example.com", newBooking.getCustomerEmail());
        assertEquals("081111111111", newBooking.getCustomerPhone());
        assertTrue(newBooking.isBreakfast());
        assertEquals(0, newBooking.getRefund());
        assertEquals(0, newBooking.getExtraPay());
        assertEquals(2, newBooking.getCapacity());
        assertEquals(created, newBooking.getCreatedDate());
        assertEquals(updated, newBooking.getUpdatedDate());
        assertEquals(room, newBooking.getRoom());
    }

    @Test
    void testBookingPrePersist() {
        Booking newBooking = new Booking();
        newBooking.onCreate();

        assertNotNull(newBooking.getBookingID());
        assertNotNull(newBooking.getCreatedDate());
        assertNotNull(newBooking.getUpdatedDate());
        assertEquals(newBooking.getCreatedDate(), newBooking.getUpdatedDate());
    }

    @Test
    void testBookingPrePersistWithExistingId() {
        Booking newBooking = new Booking();
        newBooking.setBookingID("EXISTING-ID");
        newBooking.onCreate();

        assertEquals("EXISTING-ID", newBooking.getBookingID());
        assertNotNull(newBooking.getCreatedDate());
        assertNotNull(newBooking.getUpdatedDate());
    }

    @Test
    void testBookingPreUpdate() throws InterruptedException {
        LocalDateTime originalUpdated = booking.getUpdatedDate();
        Thread.sleep(10); // Wait a bit to ensure time difference
        
        booking.onUpdate();
        
        assertNotNull(booking.getUpdatedDate());
        assertTrue(booking.getUpdatedDate().isAfter(originalUpdated) || 
                   booking.getUpdatedDate().isEqual(originalUpdated));
    }

    @Test
    void testBookingStatusTransitions() {
        // Test all status transitions (0-4)
        booking.setStatus(0); // Pending
        assertEquals(0, booking.getStatus());

        booking.setStatus(1); // Payment Confirmed
        assertEquals(1, booking.getStatus());

        booking.setStatus(2); // Checked-In
        assertEquals(2, booking.getStatus());

        booking.setStatus(3); // Cancelled
        assertEquals(3, booking.getStatus());

        booking.setStatus(4); // Completed
        assertEquals(4, booking.getStatus());
    }

    @Test
    void testBookingWithRefund() {
        booking.setStatus(3); // Cancelled
        booking.setRefund(750000);

        assertEquals(3, booking.getStatus());
        assertEquals(750000, booking.getRefund());
    }

    @Test
    void testBookingWithExtraPay() {
        booking.setExtraPay(150000);
        assertEquals(150000, booking.getExtraPay());
        
        int totalWithExtra = booking.getTotalPrice() + booking.getExtraPay();
        assertEquals(1150000, totalWithExtra);
    }

    @Test
    void testBookingEqualsAndHashCode() {
        Booking booking1 = Booking.builder()
                .bookingID("BOOK-SAME")
                .customerName("Customer 1")
                .build();

        Booking booking2 = Booking.builder()
                .bookingID("BOOK-SAME")
                .customerName("Customer 1")
                .build();

        Booking booking3 = Booking.builder()
                .bookingID("BOOK-DIFFERENT")
                .customerName("Customer 2")
                .build();

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void testBookingToString() {
        String bookingString = booking.toString();
        assertNotNull(bookingString);
        assertTrue(bookingString.contains("BOOK-001"));
        assertTrue(bookingString.contains("John Doe"));
    }

    @Test
    void testBookingRoomRelationship() {
        assertNotNull(booking.getRoom());
        assertEquals("ROOM-001", booking.getRoom().getRoomID());
        assertEquals("Room 101", booking.getRoom().getName());
        assertNotNull(booking.getRoom().getRoomType());
        assertEquals("Deluxe Room", booking.getRoom().getRoomType().getName());
    }

    @Test
    void testBookingDatesValidation() {
        LocalDateTime checkIn = LocalDateTime.of(2025, 11, 10, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2025, 11, 12, 12, 0);

        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);

        assertTrue(booking.getCheckOutDate().isAfter(booking.getCheckInDate()));
    }

    @Test
    void testBookingNullBookingId() {
        Booking newBooking = new Booking();
        newBooking.setBookingID(null);
        newBooking.onCreate();
        
        assertNotNull(newBooking.getBookingID());
        assertFalse(newBooking.getBookingID().isBlank());
    }

    @Test
    void testBookingBlankBookingId() {
        Booking newBooking = new Booking();
        newBooking.setBookingID("");
        newBooking.onCreate();
        
        assertNotNull(newBooking.getBookingID());
        assertFalse(newBooking.getBookingID().isBlank());
    }
}
