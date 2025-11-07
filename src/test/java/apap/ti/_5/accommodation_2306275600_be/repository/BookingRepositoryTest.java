package apap.ti._5.accommodation_2306275600_be.repository;

import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private Property property;
    private RoomType roomType;
    private Room room;
    private Booking booking;

    @BeforeEach
    void setUp() {
        property = Property.builder()
                .propertyID("PROP-001")
                .propertyName("Test Hotel")
                .type(1)
                .activeStatus(1)
                .income(0)
                .ownerName("Owner")
                .ownerID(UUID.randomUUID())
                .build();
        entityManager.persist(property);

        roomType = RoomType.builder()
                .roomTypeID("RT-001")
                .name("Deluxe")
                .price(500000)
                .capacity(2)
                .facility("AC, TV")
                .floor(5)
                .property(property)
                .build();
        entityManager.persist(roomType);

        room = Room.builder()
                .roomID("ROOM-001")
                .name("Room 501")
                .availabilityStatus(1)
                .activeRoom(1)
                .roomType(roomType)
                .build();
        entityManager.persist(room);

        LocalDateTime now = LocalDateTime.now();
        booking = Booking.builder()
                .bookingID("BOOK-001")
                .customerName("John Doe")
                .status(4)
                .checkInDate(now.minusDays(5))
                .checkOutDate(now.minusDays(3))
                .totalPrice(1000000)
                .extraPay(0)
                .refund(0)
                .room(room)
                .build();
        entityManager.persist(booking);
        entityManager.flush();
    }

    @Test
    void testFindConflictingBookings() {
        LocalDateTime checkIn = LocalDateTime.now().minusDays(4);
        LocalDateTime checkOut = LocalDateTime.now().minusDays(2);
        List<Booking> conflicts = bookingRepository.findConflictingBookings(room.getRoomID(), checkIn, checkOut);
        assertNotNull(conflicts);
    }

    @Test
    void testFindAllOrderedByBookingID() {
        List<Booking> bookings = bookingRepository.findAllOrderedByBookingID();
        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    void testFindByStatusOrderedByBookingID() {
        List<Booking> bookings = bookingRepository.findByStatusOrderedByBookingID(4);
        assertNotNull(bookings);
        assertTrue(bookings.stream().allMatch(b -> b.getStatus() == 4));
    }

    @Test
    void testSearchByPropertyOrRoom() {
        List<Booking> results = bookingRepository.searchByPropertyOrRoom("Test Hotel");
        assertNotNull(results);
        assertTrue(results.size() >= 1);
    }

    @Test
    void testSearchByPropertyOrRoomAndStatus() {
        List<Booking> results = bookingRepository.searchByPropertyOrRoomAndStatus("Test Hotel", 4);
        assertNotNull(results);
        assertTrue(results.stream().allMatch(b -> b.getStatus() == 4));
    }

    @Test
    void testFindBookingsToAutoCheckIn() {
        Booking pendingBooking = Booking.builder()
                .bookingID("BOOK-002")
                .customerName("Jane")
                .status(1)
                .checkInDate(LocalDateTime.now().minusDays(1))
                .checkOutDate(LocalDateTime.now().plusDays(2))
                .totalPrice(500000)
                .room(room)
                .build();
        entityManager.persist(pendingBooking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findBookingsToAutoCheckIn(LocalDateTime.now());
        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    void testFindBookingsToAutoComplete() {
        Booking checkedInBooking = Booking.builder()
                .bookingID("BOOK-003")
                .customerName("Bob")
                .status(2)
                .checkInDate(LocalDateTime.now().minusDays(3))
                .checkOutDate(LocalDateTime.now().minusDays(1))
                .totalPrice(500000)
                .room(room)
                .build();
        entityManager.persist(checkedInBooking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findBookingsToAutoComplete(LocalDateTime.now());
        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    void testFindBookingsToAutoCancel() {
        Booking pendingBooking = Booking.builder()
                .bookingID("BOOK-004")
                .customerName("Alice")
                .status(0)
                .checkInDate(LocalDateTime.now().minusDays(2))
                .checkOutDate(LocalDateTime.now().plusDays(1))
                .totalPrice(500000)
                .room(room)
                .build();
        entityManager.persist(pendingBooking);
        entityManager.flush();

        List<Booking> bookings = bookingRepository.findBookingsToAutoCancel(LocalDateTime.now());
        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    void testFindByRoom_RoomID() {
        List<Booking> bookings = bookingRepository.findByRoom_RoomID(room.getRoomID());
        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
    }

    @Test
    void testFindDoneBookingsByMonthAndYear() {
        LocalDateTime checkOut = booking.getCheckOutDate();
        List<Booking> doneBookings = bookingRepository.findDoneBookingsByMonthAndYear(
                checkOut.getMonthValue(), checkOut.getYear());
        assertNotNull(doneBookings);
        assertTrue(doneBookings.stream().allMatch(b -> b.getStatus() == 4));
    }

    @Test
    void testCountDoneBookingsByPropertyAndPeriod() {
        LocalDateTime checkOut = booking.getCheckOutDate();
        long count = bookingRepository.countDoneBookingsByPropertyAndPeriod(
                property.getPropertyID(), checkOut.getMonthValue(), checkOut.getYear());
        assertTrue(count >= 1);
    }
}

