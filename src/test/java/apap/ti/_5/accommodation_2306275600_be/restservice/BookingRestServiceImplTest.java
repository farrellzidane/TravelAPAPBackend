package apap.ti._5.accommodation_2306275600_be.restservice;

import apap.ti._5.accommodation_2306275600_be.model.Booking;
import apap.ti._5.accommodation_2306275600_be.model.Property;
import apap.ti._5.accommodation_2306275600_be.model.Room;
import apap.ti._5.accommodation_2306275600_be.model.RoomType;
import apap.ti._5.accommodation_2306275600_be.repository.BookingRepository;
import apap.ti._5.accommodation_2306275600_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306275600_be.repository.RoomRepository;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRestServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private BookingRestServiceImpl bookingRestService;

    private Room testRoom;
    private RoomType testRoomType;
    private Property testProperty;
    private Booking testBooking;
    private CreateBookingRequestDTO createBookingDTO;
    private UpdateBookingRequestDTO updateBookingDTO;
    private ChangeBookingStatusRequestDTO changeStatusDTO;

    @BeforeEach
    void setUp() {
        // Setup Property
        testProperty = new Property();
        testProperty.setPropertyID("PROP-001");
        testProperty.setPropertyName("Grand Hotel");

        // Setup RoomType
        testRoomType = new RoomType();
        testRoomType.setRoomTypeID("RT-001");
        testRoomType.setName("DELUXE");
        testRoomType.setPrice(1000000);
        testRoomType.setCapacity(2);
        testRoomType.setProperty(testProperty);

        // Setup Room
        testRoom = new Room();
        testRoom.setRoomID("ROOM-001-01");
        testRoom.setRoomType(testRoomType);
        testRoom.setMaintenanceStart(null);
        testRoom.setMaintenanceEnd(null);

        // Setup Booking
        testBooking = new Booking();
        testBooking.setBookingID("BKG-001-001-001");
        testBooking.setCheckInDate(LocalDateTime.now().plusDays(1));
        testBooking.setCheckOutDate(LocalDateTime.now().plusDays(3));
        testBooking.setTotalDays(2);
        testBooking.setTotalPrice(2000000);
        testBooking.setStatus(0); // Pending
        testBooking.setCustomerID(UUID.randomUUID());
        testBooking.setCustomerName("John Doe");
        testBooking.setCustomerEmail("john@example.com");
        testBooking.setCustomerPhone("08123456789");
        testBooking.setBreakfast(false);
        testBooking.setCapacity(2);
        testBooking.setRefund(0);
        testBooking.setExtraPay(0);
        testBooking.setRoom(testRoom);

        // Setup CreateBookingRequestDTO
        createBookingDTO = new CreateBookingRequestDTO();
        createBookingDTO.setRoomID("ROOM-001-01");
        createBookingDTO.setCheckInDate(LocalDateTime.now().plusDays(1));
        createBookingDTO.setCheckOutDate(LocalDateTime.now().plusDays(3));
        createBookingDTO.setCustomerID(UUID.randomUUID());
        createBookingDTO.setCustomerName("John Doe");
        createBookingDTO.setCustomerEmail("john@example.com");
        createBookingDTO.setCustomerPhone("08123456789");
        createBookingDTO.setIsBreakfast(false);
        createBookingDTO.setCapacity(2);

        // Setup UpdateBookingRequestDTO
        updateBookingDTO = new UpdateBookingRequestDTO();
        updateBookingDTO.setBookingID("BKG-001-001-001");
        updateBookingDTO.setRoomID("ROOM-001-01");
        updateBookingDTO.setCheckInDate(LocalDateTime.now().plusDays(2));
        updateBookingDTO.setCheckOutDate(LocalDateTime.now().plusDays(4));
        updateBookingDTO.setCustomerID(UUID.randomUUID());
        updateBookingDTO.setCustomerName("John Doe");
        updateBookingDTO.setCustomerEmail("john@example.com");
        updateBookingDTO.setCustomerPhone("08123456789");
        updateBookingDTO.setIsBreakfast(true);
        updateBookingDTO.setCapacity(2);

        // Setup ChangeBookingStatusRequestDTO
        changeStatusDTO = new ChangeBookingStatusRequestDTO();
        changeStatusDTO.setBookingID("BKG-001-001-001");
    }

    // ==================== CREATE BOOKING TESTS ====================

    @Test
    void testCreateBooking_Success() {
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(anyString(), any(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.createBooking(createBookingDTO);

        assertNotNull(result);
        assertEquals("BKG-001-001-001", result.getBookingID());
        verify(roomRepository, times(1)).findById("ROOM-001-01");
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_RoomNotFound() {
        when(roomRepository.findById("ROOM-999")).thenReturn(Optional.empty());
        createBookingDTO.setRoomID("ROOM-999");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(createBookingDTO);
        });

        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository, times(1)).findById("ROOM-999");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void testCreateBooking_CheckInDateInPast() {
        createBookingDTO.setCheckInDate(LocalDateTime.now().minusDays(1));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(createBookingDTO);
        });

        assertTrue(exception.getMessage().contains("Check-in date cannot be in the past"));
    }

    @Test
    void testCreateBooking_CheckOutBeforeCheckIn() {
        createBookingDTO.setCheckInDate(LocalDateTime.now().plusDays(3));
        createBookingDTO.setCheckOutDate(LocalDateTime.now().plusDays(1));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(createBookingDTO);
        });

        assertTrue(exception.getMessage().contains("Check-out date must be after check-in date"));
    }

    @Test
    void testCreateBooking_CapacityExceeded() {
        createBookingDTO.setCapacity(5); // RoomType capacity is 2
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(createBookingDTO);
        });

        assertTrue(exception.getMessage().contains("exceeds room type capacity"));
    }

    @Test
    void testCreateBooking_RoomInMaintenance() {
        testRoom.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        testRoom.setMaintenanceEnd(LocalDateTime.now().plusDays(5));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(createBookingDTO);
        });

        assertTrue(exception.getMessage().contains("maintenance scheduled"));
    }

    @Test
    void testCreateBooking_RoomAlreadyBooked() {
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        List<Booking> conflicts = new ArrayList<>();
        conflicts.add(testBooking);
        when(bookingRepository.findConflictingBookings(anyString(), any(), any())).thenReturn(conflicts);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(createBookingDTO);
        });

        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    void testCreateBooking_WithBreakfast() {
        createBookingDTO.setIsBreakfast(true);
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(anyString(), any(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.createBooking(createBookingDTO);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // ==================== GET ALL BOOKINGS TESTS ====================

    @Test
    void testGetAllBookings_NoFilters() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);
        
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllOrderedByBookingID()).thenReturn(bookings);

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findAllOrderedByBookingID();
    }

    @Test
    void testGetAllBookings_WithStatusFilter() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);
        
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByStatusOrderedByBookingID(0)).thenReturn(bookings);

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(0, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).findByStatusOrderedByBookingID(0);
    }

    @Test
    void testGetAllBookings_WithSearchFilter() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);
        
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.searchByPropertyOrRoom("John")).thenReturn(bookings);

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(null, "John");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).searchByPropertyOrRoom("John");
    }

    @Test
    void testGetAllBookings_WithBothFilters() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);
        
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.searchByPropertyOrRoomAndStatus("John", 0)).thenReturn(bookings);

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(0, "John");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository, times(1)).searchByPropertyOrRoomAndStatus("John", 0);
    }

    @Test
    void testGetAllBookings_EmptyResult() {
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findAllOrderedByBookingID()).thenReturn(new ArrayList<>());

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findAllOrderedByBookingID();
    }

    // ==================== GET BOOKING DETAIL TESTS ====================

    @Test
    void testGetBookingDetail_Success() {
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        BookingDetailResponseDTO result = bookingRestService.getBookingDetail("BKG-001-001-001");

        assertNotNull(result);
        assertEquals("BKG-001-001-001", result.getBookingID());
        verify(bookingRepository, times(1)).findById("BKG-001-001-001");
    }

    @Test
    void testGetBookingDetail_NotFound() {
        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findById("BKG-999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.getBookingDetail("BKG-999");
        });

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    // ==================== UPDATE BOOKING STATUS TESTS ====================

    @Test
    void testUpdateBookingStatuses_AutoCheckIn() {
        Booking pendingBooking = new Booking();
        pendingBooking.setBookingID("BKG-002");
        pendingBooking.setStatus(1); // Confirmed
        pendingBooking.setCheckInDate(LocalDateTime.now().minusDays(1));
        pendingBooking.setRoom(testRoom);

        List<Booking> toCheckIn = new ArrayList<>();
        toCheckIn.add(pendingBooking);

        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(toCheckIn);
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());

        bookingRestService.updateBookingStatuses();

        verify(bookingRepository, times(1)).findBookingsToAutoCheckIn(any());
        verify(bookingRepository, times(1)).save(pendingBooking);
        assertEquals(2, pendingBooking.getStatus()); // Should be checked-in
    }

    @Test
    void testUpdateBookingStatuses_AutoComplete() {
        Booking checkedInBooking = new Booking();
        checkedInBooking.setBookingID("BKG-003");
        checkedInBooking.setStatus(2); // Checked-in
        checkedInBooking.setCheckOutDate(LocalDateTime.now().minusDays(1));
        checkedInBooking.setRoom(testRoom);

        List<Booking> toComplete = new ArrayList<>();
        toComplete.add(checkedInBooking);

        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(toComplete);
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(new ArrayList<>());

        bookingRestService.updateBookingStatuses();

        verify(bookingRepository, times(1)).findBookingsToAutoComplete(any());
        verify(bookingRepository, times(1)).save(checkedInBooking);
        assertEquals(4, checkedInBooking.getStatus()); // Should be completed
    }

    @Test
    void testUpdateBookingStatuses_AutoCancel() {
        Booking expiredBooking = new Booking();
        expiredBooking.setBookingID("BKG-004");
        expiredBooking.setStatus(0); // Pending
        expiredBooking.setCheckInDate(LocalDateTime.now().minusDays(1));
        expiredBooking.setTotalPrice(1000000);
        expiredBooking.setRoom(testRoom);

        List<Booking> toCancel = new ArrayList<>();
        toCancel.add(expiredBooking);

        when(bookingRepository.findBookingsToAutoCheckIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoComplete(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsToAutoCancel(any())).thenReturn(toCancel);

        bookingRestService.updateBookingStatuses();

        verify(bookingRepository, times(1)).findBookingsToAutoCancel(any());
        verify(bookingRepository, times(1)).save(expiredBooking);
        assertEquals(3, expiredBooking.getStatus()); // Should be cancelled
    }

    // ==================== GET BOOKING FOR UPDATE TESTS ====================

    @Test
    void testGetBookingForUpdate_Success() {
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        BookingUpdateFormDTO result = bookingRestService.getBookingForUpdate("BKG-001-001-001");

        assertNotNull(result);
        assertEquals("BKG-001-001-001", result.getBookingID());
        verify(bookingRepository, times(1)).findById("BKG-001-001-001");
    }

    @Test
    void testGetBookingForUpdate_NotFound() {
        when(bookingRepository.findById("BKG-999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.getBookingForUpdate("BKG-999");
        });

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    void testGetBookingForUpdate_CannotUpdate() {
        testBooking.setStatus(2); // Checked-in - cannot update (must be 0 or 1)
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.getBookingForUpdate("BKG-001-001-001");
        });

        assertTrue(exception.getMessage().contains("Pending") || exception.getMessage().contains("Confirmed"));
    }

    // ==================== UPDATE BOOKING TESTS ====================

    @Test
    void testUpdateBooking_Success() {
        testBooking.setStatus(1); // Confirmed - can be updated
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(anyString(), any(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.updateBooking(updateBookingDTO);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testUpdateBooking_NotFound() {
        when(bookingRepository.findById("BKG-999")).thenReturn(Optional.empty());
        updateBookingDTO.setBookingID("BKG-999");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.updateBooking(updateBookingDTO);
        });

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    void testUpdateBooking_CannotUpdateCompletedBooking() {
        testBooking.setStatus(4); // Completed - cannot update
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.updateBooking(updateBookingDTO);
        });

        assertTrue(exception.getMessage().contains("Pending") || exception.getMessage().contains("Confirmed"));
    }

    // ==================== PAY BOOKING TESTS ====================

    @Test
    void testPayBooking_Success() {
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        BookingResponseDTO result = bookingRestService.payBooking(changeStatusDTO);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testPayBooking_NotFound() {
        when(bookingRepository.findById("BKG-999")).thenReturn(Optional.empty());
        changeStatusDTO.setBookingID("BKG-999");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.payBooking(changeStatusDTO);
        });

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    void testPayBooking_InvalidStatus() {
        testBooking.setStatus(2); // Checked-in - cannot pay unless has extra pay
        testBooking.setExtraPay(0); // No extra pay
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.payBooking(changeStatusDTO);
        });

        assertTrue(exception.getMessage().contains("does not require payment"));
    }

    // ==================== CANCEL BOOKING TESTS ====================

    @Test
    void testCancelBooking_Success() {
        testBooking.setStatus(1); // Confirmed
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        BookingResponseDTO result = bookingRestService.cancelBooking(changeStatusDTO);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_NotFound() {
        when(bookingRepository.findById("BKG-999")).thenReturn(Optional.empty());
        changeStatusDTO.setBookingID("BKG-999");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.cancelBooking(changeStatusDTO);
        });

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    void testCancelBooking_InvalidStatus() {
        testBooking.setStatus(4); // Completed - cannot cancel
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.cancelBooking(changeStatusDTO);
        });

        assertTrue(exception.getMessage().contains("Cannot cancel"));
    }

    // ==================== REFUND BOOKING TESTS ====================

    @Test
    void testRefundBooking_Success() {
        testBooking.setStatus(3); // Cancelled
        testBooking.setRefund(500000);
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);

        BookingResponseDTO result = bookingRestService.refundBooking(changeStatusDTO);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testRefundBooking_NotFound() {
        when(bookingRepository.findById("BKG-999")).thenReturn(Optional.empty());
        changeStatusDTO.setBookingID("BKG-999");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.refundBooking(changeStatusDTO);
        });

        assertTrue(exception.getMessage().contains("Booking not found"));
    }

    @Test
    void testRefundBooking_InvalidStatus() {
        testBooking.setStatus(0); // Pending
        testBooking.setRefund(0); // No refund available
        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.refundBooking(changeStatusDTO);
        });

        assertTrue(exception.getMessage().contains("No refund available"));
    }

    // ==================== GET BOOKING STATISTICS TESTS ====================

    @Test
    void testGetBookingStatistics_Success() {
        List<Booking> bookings = new ArrayList<>();
        testBooking.setStatus(4); // Completed
        bookings.add(testBooking);

        when(bookingRepository.findDoneBookingsByMonthAndYear(anyInt(), anyInt())).thenReturn(bookings);

        BookingChartResponseDTO result = bookingRestService.getBookingStatistics(11, 2025);

        assertNotNull(result);
        assertNotNull(result.getPropertyRevenues());
        verify(bookingRepository, times(1)).findDoneBookingsByMonthAndYear(11, 2025);
    }

    @Test
    void testGetBookingStatistics_NoData() {
        when(bookingRepository.findDoneBookingsByMonthAndYear(anyInt(), anyInt())).thenReturn(new ArrayList<>());

        BookingChartResponseDTO result = bookingRestService.getBookingStatistics(11, 2025);

        assertNotNull(result);
        assertNotNull(result.getPropertyRevenues());
        assertTrue(result.getPropertyRevenues().isEmpty());
    }

    @Test
    void testGetBookingStatistics_CurrentMonth() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(testBooking);

        when(bookingRepository.findDoneBookingsByMonthAndYear(anyInt(), anyInt())).thenReturn(bookings);

        BookingChartResponseDTO result = bookingRestService.getBookingStatistics(now.getMonthValue(), now.getYear());

        assertNotNull(result);
        verify(bookingRepository, times(1)).findDoneBookingsByMonthAndYear(now.getMonthValue(), now.getYear());
    }

    // ============ ADDITIONAL TESTS FOR HIGHER COVERAGE ============

    // Cancel Booking - Additional Scenarios
    @Test
    void testCancelBooking_Status1_FullRefund() {
        testBooking.setStatus(1); // Confirmed
        testBooking.setTotalPrice(1000);
        testBooking.setExtraPay(0);
        testBooking.setRefund(0);
        testProperty.setIncome(5000);

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.cancelBooking(dto);

        assertNotNull(result);
        assertEquals("BKG-001-001-001", result.getBookingID());
        assertEquals(3, testBooking.getStatus()); // Status changed to 3 (Cancelled)
        assertEquals(1000, testBooking.getRefund());
        verify(bookingRepository, times(1)).save(testBooking);
        verify(propertyRepository, times(1)).save(testProperty);
    }

    @Test
    void testCancelBooking_Status3_RequestRefund() {
        testBooking.setStatus(3); // Request Refund
        testBooking.setTotalPrice(2000);
        testBooking.setExtraPay(0);
        testBooking.setRefund(500);
        testProperty.setIncome(5000);

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.cancelBooking(dto);

        assertNotNull(result);
        assertEquals("BKG-001-001-001", result.getBookingID());
        assertEquals(3, testBooking.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
        verify(propertyRepository, times(1)).save(testProperty);
    }

    @Test
    void testCancelBooking_InvalidStatus_Completed() {
        testBooking.setStatus(5); // Completed - cannot cancel

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.cancelBooking(dto);
        });

        assertTrue(exception.getMessage().contains("cannot") || exception.getMessage().contains("Cannot"));
    }

    @Test
    void testCancelBooking_Status0_WithExtraPay() {
        testBooking.setStatus(0); // Pending
        testBooking.setTotalPrice(2000);
        testBooking.setExtraPay(300);
        testBooking.setRefund(0);
        testProperty.setIncome(5000);

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.cancelBooking(dto);

        assertNotNull(result);
        assertEquals(1700, testBooking.getRefund()); // 2000 - 300
        verify(bookingRepository, times(1)).save(testBooking);
    }

    // Pay Booking - ExtraPay Scenario
    @Test
    void testPayBooking_WithExtraPay() {
        testBooking.setStatus(3); // Checked In
        testBooking.setExtraPay(500); // Has extra payment

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.payBooking(dto);

        assertNotNull(result);
        assertEquals("BKG-001-001-001", result.getBookingID());
        assertEquals(0, testBooking.getExtraPay());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void testPayBooking_Status0_InitialPayment() {
        testBooking.setStatus(0); // Pending Payment
        testBooking.setExtraPay(0);
        testProperty.setIncome(1000);

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.payBooking(dto);

        assertNotNull(result);
        assertEquals(1, testBooking.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
        verify(propertyRepository, times(1)).save(testProperty);
    }

    @Test
    void testPayBooking_InvalidStatus_NoExtraPay() {
        testBooking.setStatus(2); // Check-in status
        testBooking.setExtraPay(0); // No extra payment

        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        dto.setBookingID("BKG-001-001-001");

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.payBooking(dto);
        });

        assertTrue(exception.getMessage().contains("does not require payment") || 
                  exception.getMessage().contains("cannot") || 
                  exception.getMessage().contains("Cannot"));
    }

    // Update Booking - Edge Cases
    @Test
    void testUpdateBooking_RoomNotFound() {
        testBooking.setStatus(0);

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingID("BKG-001-001-001");
        dto.setRoomID("NON-EXISTENT-ROOM");
        dto.setCheckInDate(LocalDateTime.now().plusDays(1));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(2));
        dto.setCustomerName("Jane Doe");
        dto.setCustomerEmail("jane@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setIsBreakfast(true);
        dto.setCapacity(2);

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("NON-EXISTENT-ROOM")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.updateBooking(dto);
        });

        assertTrue(exception.getMessage().contains("Room") && exception.getMessage().contains("not found"));
    }

    @Test
    void testUpdateBooking_CapacityExceeded() {
        testBooking.setStatus(0);
        testRoomType.setCapacity(2); // Room capacity is 2

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingID("BKG-001-001-001");
        dto.setRoomID("ROOM-001-01");
        dto.setCheckInDate(LocalDateTime.now().plusDays(1));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(2));
        dto.setCustomerName("Jane Doe");
        dto.setCustomerEmail("jane@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setIsBreakfast(true);
        dto.setCapacity(5); // Exceeds capacity

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.updateBooking(dto);
        });

        assertTrue(exception.getMessage().contains("exceeds") || exception.getMessage().contains("capacity"));
    }

    @Test
    void testUpdateBooking_RoomInMaintenance() {
        testBooking.setStatus(0);
        testRoom.setMaintenanceStart(LocalDateTime.now().plusDays(1));
        testRoom.setMaintenanceEnd(LocalDateTime.now().plusDays(5));

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingID("BKG-001-001-001");
        dto.setRoomID("ROOM-001-01");
        dto.setCheckInDate(LocalDateTime.now().plusDays(2));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(3));
        dto.setCustomerName("Jane Doe");
        dto.setCustomerEmail("jane@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setIsBreakfast(false);
        dto.setCapacity(2);

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.updateBooking(dto);
        });

        assertTrue(exception.getMessage().contains("maintenance"));
    }

    @Test
    void testUpdateBooking_ConflictingBooking() {
        testBooking.setStatus(0);
        testBooking.setBookingID("BKG-001-001-001");
        testRoom.setMaintenanceStart(null);
        testRoom.setMaintenanceEnd(null);

        // Create a conflicting booking with proper room relationship
        Room conflictRoom = new Room();
        conflictRoom.setRoomID("ROOM-001-01");
        conflictRoom.setRoomType(testRoomType);
        
        Booking conflictingBooking = new Booking();
        conflictingBooking.setBookingID("BKG-001-001-002"); // Different ID
        conflictingBooking.setRoom(conflictRoom);
        conflictingBooking.setCheckInDate(LocalDateTime.now().plusDays(1).plusHours(12));
        conflictingBooking.setCheckOutDate(LocalDateTime.now().plusDays(3));
        conflictingBooking.setStatus(1);

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingID("BKG-001-001-001");
        dto.setRoomID("ROOM-001-01");
        dto.setCheckInDate(LocalDateTime.now().plusDays(1));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(2));
        dto.setCustomerName("Jane Doe");
        dto.setCustomerEmail("jane@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setIsBreakfast(false);
        dto.setCapacity(2);

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        // Return only the conflicting booking (current booking will be filtered out by implementation)
        when(bookingRepository.findConflictingBookings(eq("ROOM-001-01"), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(new ArrayList<>(List.of(conflictingBooking)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.updateBooking(dto);
        });

        assertTrue(exception.getMessage().contains("booked") || 
                  exception.getMessage().contains("conflict") || 
                  exception.getMessage().contains("overlaps") ||
                  exception.getMessage().contains("already"));
    }

    @Test
    void testUpdateBooking_PriceDecrease_WithRefund() {
        testBooking.setStatus(1);
        testBooking.setTotalPrice(2000);
        testBooking.setRefund(0);
        testBooking.setExtraPay(0);
        testRoom.setMaintenanceStart(null);
        testRoom.setMaintenanceEnd(null);
        testRoomType.setPrice(500); // Lower price
        testProperty.setIncome(3000);

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingID("BKG-001-001-001");
        dto.setRoomID("ROOM-001-01");
        dto.setCheckInDate(LocalDateTime.now().plusDays(1));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(2)); // 1 day
        dto.setCustomerName("Jane Doe");
        dto.setCustomerEmail("jane@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setIsBreakfast(false);
        dto.setCapacity(2);

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(anyString(), any(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.updateBooking(dto);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(testBooking);
    }

    // Helper Methods Coverage
    @Test
    void testGetBookingForUpdate_CannotUpdateStatus4() {
        testBooking.setStatus(4); // Cancelled

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.getBookingForUpdate("BKG-001-001-001");
        });

        assertTrue(exception.getMessage().contains("cannot") || exception.getMessage().contains("Cannot") || exception.getMessage().contains("only"));
    }

    @Test
    void testGetBookingForUpdate_CannotUpdateStatus5() {
        testBooking.setStatus(5); // Completed

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.getBookingForUpdate("BKG-001-001-001");
        });

        assertTrue(exception.getMessage().contains("cannot") || exception.getMessage().contains("Cannot") || exception.getMessage().contains("only"));
    }

    @Test
    void testGetBookingForUpdate_Status0WithExtraPay() {
        testBooking.setStatus(0); // Pending
        testBooking.setExtraPay(500); // Has extra pay

        when(bookingRepository.findById("BKG-001-001-001")).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.getBookingForUpdate("BKG-001-001-001");
        });

        assertTrue(exception.getMessage().contains("extra") || exception.getMessage().contains("pay"));
    }

    // CreateBooking - Edge Cases
    @Test
    void testCreateBooking_ConflictWithExistingBooking() {
        // Create existing booking that conflicts
        Booking existingBooking = new Booking();
        existingBooking.setBookingID("BKG-001-001-002");
        existingBooking.setRoom(testRoom);
        existingBooking.setCheckInDate(LocalDateTime.now().plusDays(1).plusHours(12));
        existingBooking.setCheckOutDate(LocalDateTime.now().plusDays(3));
        existingBooking.setStatus(1);

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setRoomID("ROOM-001-01");
        dto.setCheckInDate(LocalDateTime.now().plusDays(1));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(2));
        dto.setCustomerName("John Doe");
        dto.setCustomerEmail("john@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setCapacity(2);
        dto.setIsBreakfast(false);
        dto.setCustomerID(UUID.randomUUID());

        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(anyString(), any(), any())).thenReturn(List.of(existingBooking));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(dto);
        });

        assertTrue(exception.getMessage().contains("booked") || exception.getMessage().contains("conflict"));
    }

    @Test
    void testCreateBooking_InvalidCheckOutDate() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setRoomID("ROOM-001-01");
        dto.setCheckInDate(LocalDateTime.now().plusDays(5));
        dto.setCheckOutDate(LocalDateTime.now().plusDays(5)); // Same day
        dto.setCustomerName("John Doe");
        dto.setCustomerEmail("john@example.com");
        dto.setCustomerPhone("08123456789");
        dto.setCapacity(2);
        dto.setIsBreakfast(false);
        dto.setCustomerID(UUID.randomUUID());

        when(roomRepository.findById("ROOM-001-01")).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingRestService.createBooking(dto);
        });

        assertTrue(exception.getMessage().contains("must be after check-in"));
    }
}
