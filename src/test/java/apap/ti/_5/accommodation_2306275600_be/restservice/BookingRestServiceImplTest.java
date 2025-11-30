package apap.ti._5.accommodation_2306275600_be.restservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import apap.ti._5.accommodation_2306275600_be.service.BillIntegrationService;

@ExtendWith(MockitoExtension.class)
class BookingRestServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private BillIntegrationService billIntegrationService;

    @InjectMocks
    private BookingRestServiceImpl bookingRestService;

    private Booking testBooking;
    private Room testRoom;
    private RoomType testRoomType;
    private Property testProperty;
    private UUID bookingId;
    private UUID roomId;
    private String customerId;
    private UUID customerIdUUID;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        customerIdUUID = UUID.randomUUID();
        customerId = customerIdUUID.toString();

        testProperty = Property.builder()
            .propertyID(UUID.randomUUID())
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
            .property(testProperty)
            .build();

        testRoom = Room.builder()
            .roomID(roomId)
            .name("101")
            .availabilityStatus(1)
            .activeRoom(1)
            .roomType(testRoomType)
            .build();

        testBooking = Booking.builder()
            .bookingID(bookingId)
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .totalDays(2)
            .totalPrice(1000000)
            .status(0)
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .room(testRoom)
            .createdDate(LocalDateTime.now())
            .build();
    }

    // ============================================
    // CREATE BOOKING TESTS
    // ============================================

    @Test
    void testCreateBooking_Success() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.createBooking(requestDTO);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
        assertEquals(customerIdUUID, result.getCustomerID());
        verify(roomRepository).findById(roomId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_RoomNotFound_ThrowsException() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.createBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Room not found"));
        verify(roomRepository).findById(roomId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_CheckInDateInPast_ThrowsException() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().minusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(1))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.createBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Check-in date cannot be in the past"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_CheckOutBeforeCheckIn_ThrowsException() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(3))
            .checkOutDate(LocalDateTime.now().plusDays(1))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.createBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Check-out date must be after check-in date"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ExceedCapacity_ThrowsException() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(5)
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.createBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("exceeds room type capacity"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_RoomHasConflict_ThrowsException() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(false)
            .capacity(2)
            .build();

        Booking conflictBooking = Booking.builder()
            .bookingID(UUID.randomUUID())
            .checkInDate(LocalDateTime.now().plusDays(2))
            .checkOutDate(LocalDateTime.now().plusDays(4))
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(any(), any(), any()))
            .thenReturn(Arrays.asList(conflictBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.createBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Room is already booked"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_WithBreakfast_CalculatesPriceCorrectly() {
        CreateBookingRequestDTO requestDTO = CreateBookingRequestDTO.builder()
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(1))
            .checkOutDate(LocalDateTime.now().plusDays(3))
            .customerID(customerIdUUID)
            .customerName("Test Customer")
            .customerEmail("test@example.com")
            .customerPhone("08123456789")
            .isBreakfast(true)
            .capacity(2)
            .build();

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setBookingID(UUID.randomUUID());
            return savedBooking;
        });

        BookingResponseDTO result = bookingRestService.createBooking(requestDTO);

        assertNotNull(result);
        // Base: 500000 * 2 days = 1000000, Breakfast: 50000 * 2 = 100000, Total = 1100000
        verify(bookingRepository).save(argThat(booking -> 
            booking.getTotalPrice() == 1100000 && booking.isBreakfast()
        ));
    }

    // ============================================
    // GET ALL BOOKINGS TESTS
    // ============================================

    @Test
    void testGetAllBookings_NoFilter_ReturnsAll() {
        when(bookingRepository.findAllOrderedByBookingID()).thenReturn(Arrays.asList(testBooking));

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookingId, result.get(0).getBookingID());
        verify(bookingRepository).findAllOrderedByBookingID();
    }

    @Test
    void testGetAllBookings_FilterByStatus() {
        when(bookingRepository.findByStatusOrderedByBookingID(0)).thenReturn(Arrays.asList(testBooking));

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(0, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findByStatusOrderedByBookingID(0);
    }

    @Test
    void testGetAllBookings_FilterBySearch() {
        when(bookingRepository.searchByPropertyOrRoom("Test")).thenReturn(Arrays.asList(testBooking));

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(null, "Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).searchByPropertyOrRoom("Test");
    }

    @Test
    void testGetAllBookings_FilterByStatusAndSearch() {
        when(bookingRepository.searchByPropertyOrRoomAndStatus("Test", 0)).thenReturn(Arrays.asList(testBooking));

        List<BookingListItemDTO> result = bookingRestService.getAllBookings(0, "Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).searchByPropertyOrRoomAndStatus("Test", 0);
    }

    // ============================================
    // GET BOOKING DETAIL TESTS
    // ============================================

    @Test
    void testGetBookingDetail_Success() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        BookingDetailResponseDTO result = bookingRestService.getBookingDetail(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
        assertEquals("Test Hotel", result.getPropertyName());
        assertEquals(customerIdUUID, result.getCustomerID());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetBookingDetail_NotFound_ThrowsException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.getBookingDetail(bookingId));
        
        assertTrue(exception.getMessage().contains("Booking not found"));
        verify(bookingRepository).findById(bookingId);
    }

    // ============================================
    // GET BOOKING FOR UPDATE TESTS
    // ============================================

    @Test
    void testGetBookingForUpdate_Success() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        BookingUpdateFormDTO result = bookingRestService.getBookingForUpdate(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingID());
        assertEquals(roomId.toString(), result.getRoomID());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetBookingForUpdate_StatusNotPending_ThrowsException() {
        testBooking.setStatus(1);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.getBookingForUpdate(bookingId));
        
        assertTrue(exception.getMessage().contains("Can only update bookings with status 'Waiting for Payment'"));
        verify(bookingRepository).findById(bookingId);
    }

    // ============================================
    // UPDATE BOOKING TESTS
    // ============================================

    @Test
    void testUpdateBooking_Success() {
        UpdateBookingRequestDTO requestDTO = UpdateBookingRequestDTO.builder()
            .bookingID(bookingId)
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(2))
            .checkOutDate(LocalDateTime.now().plusDays(4))
            .customerID(customerIdUUID)
            .customerName("Updated Customer")
            .customerEmail("updated@example.com")
            .customerPhone("08987654321")
            .isBreakfast(true)
            .capacity(2)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(bookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.updateBooking(requestDTO);

        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testUpdateBooking_StatusNotPending_ThrowsException() {
        testBooking.setStatus(1);
        
        UpdateBookingRequestDTO requestDTO = UpdateBookingRequestDTO.builder()
            .bookingID(bookingId)
            .roomID(roomId.toString())
            .checkInDate(LocalDateTime.now().plusDays(2))
            .checkOutDate(LocalDateTime.now().plusDays(4))
            .customerID(customerIdUUID)
            .customerName("Updated Customer")
            .customerEmail("updated@example.com")
            .customerPhone("08987654321")
            .isBreakfast(true)
            .capacity(2)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.updateBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Cannot update booking after payment has been confirmed"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ============================================
    // PAY BOOKING TESTS
    // ============================================

    @Test
    void testPayBooking_Success() {
        ChangeBookingStatusRequestDTO requestDTO = ChangeBookingStatusRequestDTO.builder()
            .bookingID(bookingId)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(propertyRepository.save(any(Property.class))).thenReturn(testProperty);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.payBooking(requestDTO);

        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
        verify(propertyRepository).save(any(Property.class));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testPayBooking_StatusNotPending_ThrowsException() {
        testBooking.setStatus(1);
        
        ChangeBookingStatusRequestDTO requestDTO = ChangeBookingStatusRequestDTO.builder()
            .bookingID(bookingId)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.payBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("does not require payment or has already been paid"));
        verify(propertyRepository, never()).save(any(Property.class));
    }

    // ============================================
    // CANCEL BOOKING TESTS
    // ============================================

    @Test
    void testCancelBooking_Success() {
        ChangeBookingStatusRequestDTO requestDTO = ChangeBookingStatusRequestDTO.builder()
            .bookingID(bookingId)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        BookingResponseDTO result = bookingRestService.cancelBooking(requestDTO);

        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCancelBooking_StatusNotPending_ThrowsException() {
        testBooking.setStatus(1);
        
        ChangeBookingStatusRequestDTO requestDTO = ChangeBookingStatusRequestDTO.builder()
            .bookingID(bookingId)
            .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.cancelBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Cannot cancel booking that has been paid"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ============================================
    // REFUND BOOKING TESTS
    // ============================================

    @Test
    void testRefundBooking_ThrowsException() {
        ChangeBookingStatusRequestDTO requestDTO = ChangeBookingStatusRequestDTO.builder()
            .bookingID(bookingId)
            .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.refundBooking(requestDTO));
        
        assertTrue(exception.getMessage().contains("Refund feature has been removed"));
    }

    // ============================================
    // GET BOOKING STATISTICS TESTS
    // ============================================

    @Test
    void testGetBookingStatistics_Success() {
        testBooking.setStatus(1); // Payment confirmed
        
        when(bookingRepository.findDoneBookingsByMonthAndYear(12, 2025))
            .thenReturn(Arrays.asList(testBooking));

        BookingChartResponseDTO result = bookingRestService.getBookingStatistics(12, 2025);

        assertNotNull(result);
        assertEquals(12, result.getMonth());
        assertEquals(2025, result.getYear());
        assertTrue(result.getTotalRevenue() > 0);
        verify(bookingRepository).findDoneBookingsByMonthAndYear(12, 2025);
    }

    @Test
    void testGetBookingStatistics_UseCurrentMonthYear() {
        LocalDateTime now = LocalDateTime.now();
        
        when(bookingRepository.findDoneBookingsByMonthAndYear(now.getMonthValue(), now.getYear()))
            .thenReturn(Arrays.asList());

        BookingChartResponseDTO result = bookingRestService.getBookingStatistics(null, null);

        assertNotNull(result);
        assertEquals(now.getMonthValue(), result.getMonth());
        assertEquals(now.getYear(), result.getYear());
        verify(bookingRepository).findDoneBookingsByMonthAndYear(now.getMonthValue(), now.getYear());
    }

    @Test
    void testGetBookingStatistics_InvalidMonth_ThrowsException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.getBookingStatistics(13, 2025));
        
        assertTrue(exception.getMessage().contains("Invalid month"));
    }

    @Test
    void testGetBookingStatistics_InvalidYear_ThrowsException() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookingRestService.getBookingStatistics(12, 1999));
        
        assertTrue(exception.getMessage().contains("Invalid year"));
    }
}
