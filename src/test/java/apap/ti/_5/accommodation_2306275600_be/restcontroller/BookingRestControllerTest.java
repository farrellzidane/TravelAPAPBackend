package apap.ti._5.accommodation_2306275600_be.restcontroller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.BookingRestServiceRBAC;

@ExtendWith(MockitoExtension.class)
class BookingRestControllerTest {

    @Mock
    private BookingRestServiceRBAC bookingRestService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private BookingRestController bookingRestController;

    private UUID testBookingId;
    private BookingListItemDTO mockBookingListItem;
    private BookingDetailResponseDTO mockBookingDetail;
    private BookingResponseDTO mockBookingResponse;
    private BookingUpdateFormDTO mockBookingUpdateForm;
    private BookingChartResponseDTO mockBookingChart;

    @BeforeEach
    void setUp() {
        testBookingId = UUID.randomUUID();
        
        mockBookingListItem = new BookingListItemDTO();
        mockBookingListItem.setBookingID(testBookingId);
        
        mockBookingDetail = new BookingDetailResponseDTO();
        mockBookingDetail.setBookingID(testBookingId);
        
        mockBookingResponse = new BookingResponseDTO();
        mockBookingResponse.setBookingID(testBookingId);
        mockBookingResponse.setStatus(0);
        
        mockBookingUpdateForm = new BookingUpdateFormDTO();
        mockBookingUpdateForm.setBookingID(testBookingId);
        
        mockBookingChart = new BookingChartResponseDTO();
        mockBookingChart.setPeriod("December 2025");
    }

    // ========== GET ALL BOOKINGS TESTS ==========
    
    @Test
    void testGetAllBookings_Success() {
        // Arrange
        List<BookingListItemDTO> bookings = Arrays.asList(mockBookingListItem);
        when(bookingRestService.getAllBookings(null, null)).thenReturn(bookings);

        // Act
        ResponseEntity<BaseResponseDTO<List<BookingListItemDTO>>> response = 
            bookingRestController.getAllBookings(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved"));
        verify(bookingRestService, times(1)).getAllBookings(null, null);
    }

    @Test
    void testGetAllBookings_WithStatusFilter() {
        // Arrange
        List<BookingListItemDTO> bookings = Arrays.asList(mockBookingListItem);
        when(bookingRestService.getAllBookings(1, null)).thenReturn(bookings);

        // Act
        ResponseEntity<BaseResponseDTO<List<BookingListItemDTO>>> response = 
            bookingRestController.getAllBookings(1, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingRestService, times(1)).getAllBookings(1, null);
    }

    @Test
    void testGetAllBookings_WithSearchFilter() {
        // Arrange
        List<BookingListItemDTO> bookings = Arrays.asList(mockBookingListItem);
        when(bookingRestService.getAllBookings(null, "test")).thenReturn(bookings);

        // Act
        ResponseEntity<BaseResponseDTO<List<BookingListItemDTO>>> response = 
            bookingRestController.getAllBookings(null, "test");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingRestService, times(1)).getAllBookings(null, "test");
    }

    @Test
    void testGetAllBookings_EmptyList() {
        // Arrange
        List<BookingListItemDTO> emptyList = new ArrayList<>();
        when(bookingRestService.getAllBookings(null, null)).thenReturn(emptyList);

        // Act
        ResponseEntity<BaseResponseDTO<List<BookingListItemDTO>>> response = 
            bookingRestController.getAllBookings(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getData().size());
        assertTrue(response.getBody().getMessage().contains("0 booking(s)"));
    }

    @Test
    void testGetAllBookings_Exception() {
        // Arrange
        when(bookingRestService.getAllBookings(null, null))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<BaseResponseDTO<List<BookingListItemDTO>>> response = 
            bookingRestController.getAllBookings(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Failed to retrieve bookings"));
    }

    // ========== GET BOOKING DETAIL TESTS ==========
    
    @Test
    void testGetBookingDetail_Success() {
        // Arrange
        when(bookingRestService.getBookingDetail(testBookingId)).thenReturn(mockBookingDetail);

        // Act
        ResponseEntity<BaseResponseDTO<BookingDetailResponseDTO>> response = 
            bookingRestController.getBookingDetail(testBookingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData());
        assertEquals(testBookingId, response.getBody().getData().getBookingID());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved booking detail"));
        verify(bookingRestService, times(1)).getBookingDetail(testBookingId);
    }

    @Test
    void testGetBookingDetail_NotFound() {
        // Arrange
        when(bookingRestService.getBookingDetail(testBookingId))
            .thenThrow(new RuntimeException("Booking not found"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingDetailResponseDTO>> response = 
            bookingRestController.getBookingDetail(testBookingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Booking not found"));
    }

    // ========== CREATE BOOKING TESTS ==========
    
    @Test
    void testCreateBooking_Success() {
        // Arrange
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.createBooking(dto)).thenReturn(mockBookingResponse);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.createBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Booking berhasil dibuat"));
        verify(bookingRestService, times(1)).createBooking(dto);
    }

    @Test
    void testCreateBooking_ValidationError() {
        // Arrange
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        FieldError fieldError = new FieldError("dto", "field", "Field is required");
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.createBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Field is required"));
        verify(bookingRestService, never()).createBooking(any());
    }

    @Test
    void testCreateBooking_MultipleValidationErrors() {
        // Arrange
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        List<FieldError> errors = Arrays.asList(
            new FieldError("dto", "field1", "Error 1"),
            new FieldError("dto", "field2", "Error 2")
        );
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(errors);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.createBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error 1"));
        assertTrue(response.getBody().getMessage().contains("Error 2"));
    }

    @Test
    void testCreateBooking_RuntimeException() {
        // Arrange
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.createBooking(dto))
            .thenThrow(new RuntimeException("Invalid booking data"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.createBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Booking gagal dibuat"));
    }

    // ========== GET BOOKING FOR UPDATE TESTS ==========
    
    @Test
    void testGetBookingForUpdate_Success() {
        // Arrange
        when(bookingRestService.getBookingForUpdate(testBookingId))
            .thenReturn(mockBookingUpdateForm);

        // Act
        ResponseEntity<BaseResponseDTO<BookingUpdateFormDTO>> response = 
            bookingRestController.getBookingForUpdate(testBookingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved booking for update"));
        verify(bookingRestService, times(1)).getBookingForUpdate(testBookingId);
    }

    @Test
    void testGetBookingForUpdate_RuntimeException() {
        // Arrange
        when(bookingRestService.getBookingForUpdate(testBookingId))
            .thenThrow(new RuntimeException("Cannot update paid booking"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingUpdateFormDTO>> response = 
            bookingRestController.getBookingForUpdate(testBookingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Cannot update booking"));
    }

    // ========== UPDATE BOOKING TESTS ==========
    
    @Test
    void testUpdateBooking_Success() {
        // Arrange
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.updateBooking(dto)).thenReturn(mockBookingResponse);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.updateBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Booking berhasil diubah"));
        verify(bookingRestService, times(1)).updateBooking(dto);
    }

    @Test
    void testUpdateBooking_ValidationError() {
        // Arrange
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        FieldError fieldError = new FieldError("dto", "checkInDate", "Invalid date");
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.updateBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Invalid date"));
        verify(bookingRestService, never()).updateBooking(any());
    }

    @Test
    void testUpdateBooking_RuntimeException() {
        // Arrange
        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.updateBooking(dto))
            .thenThrow(new RuntimeException("Invalid update"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.updateBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Booking gagal diubah"));
    }

    // ========== PAY BOOKING TESTS ==========
    
    @Test
    void testPayBooking_Success() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        mockBookingResponse.setStatus(1);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.payBooking(dto)).thenReturn(mockBookingResponse);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.payBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Pembayaran berhasil"));
        assertTrue(response.getBody().getMessage().contains("Payment Confirmed"));
        verify(bookingRestService, times(1)).payBooking(dto);
    }

    @Test
    void testPayBooking_SuccessWithoutStatusChange() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        mockBookingResponse.setStatus(0);
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.payBooking(dto)).thenReturn(mockBookingResponse);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.payBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Pembayaran berhasil"));
        assertFalse(response.getBody().getMessage().contains("Payment Confirmed"));
    }

    @Test
    void testPayBooking_ValidationError() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        FieldError fieldError = new FieldError("dto", "bookingId", "Required");
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.payBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bookingRestService, never()).payBooking(any());
    }

    @Test
    void testPayBooking_RuntimeException() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.payBooking(dto))
            .thenThrow(new RuntimeException("Payment failed"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.payBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Pembayaran gagal"));
    }

    // ========== CANCEL BOOKING TESTS ==========
    
    @Test
    void testCancelBooking_Success() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.cancelBooking(dto)).thenReturn(mockBookingResponse);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.cancelBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("berhasil dibatalkan"));
        assertTrue(response.getBody().getMessage().contains("Cancelled"));
        verify(bookingRestService, times(1)).cancelBooking(dto);
    }

    @Test
    void testCancelBooking_ValidationError() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        FieldError fieldError = new FieldError("dto", "bookingId", "Required");
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.cancelBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bookingRestService, never()).cancelBooking(any());
    }

    @Test
    void testCancelBooking_RuntimeException() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.cancelBooking(dto))
            .thenThrow(new RuntimeException("Cannot cancel"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.cancelBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Pembatalan gagal"));
    }

    // ========== REFUND BOOKING TESTS ==========
    
    @Test
    void testRefundBooking_Success() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.refundBooking(dto)).thenReturn(mockBookingResponse);

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.refundBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Refund berhasil"));
        verify(bookingRestService, times(1)).refundBooking(dto);
    }

    @Test
    void testRefundBooking_ValidationError() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        FieldError fieldError = new FieldError("dto", "bookingId", "Required");
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.refundBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bookingRestService, never()).refundBooking(any());
    }

    @Test
    void testRefundBooking_RuntimeException() {
        // Arrange
        ChangeBookingStatusRequestDTO dto = new ChangeBookingStatusRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(bookingRestService.refundBooking(dto))
            .thenThrow(new RuntimeException("Refund failed"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingResponseDTO>> response = 
            bookingRestController.refundBooking(dto, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Refund gagal"));
    }

    // ========== GET BOOKING STATISTICS TESTS ==========
    
    @Test
    void testGetBookingStatistics_Success() {
        // Arrange
        when(bookingRestService.getBookingStatistics(null, null))
            .thenReturn(mockBookingChart);

        // Act
        ResponseEntity<BaseResponseDTO<BookingChartResponseDTO>> response = 
            bookingRestController.getBookingStatistics(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getMessage().contains("Successfully retrieved booking statistics"));
        assertTrue(response.getBody().getMessage().contains("December 2025"));
        verify(bookingRestService, times(1)).getBookingStatistics(null, null);
    }

    @Test
    void testGetBookingStatistics_WithMonthAndYear() {
        // Arrange
        when(bookingRestService.getBookingStatistics(12, 2025))
            .thenReturn(mockBookingChart);

        // Act
        ResponseEntity<BaseResponseDTO<BookingChartResponseDTO>> response = 
            bookingRestController.getBookingStatistics(12, 2025);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookingRestService, times(1)).getBookingStatistics(12, 2025);
    }

    @Test
    void testGetBookingStatistics_RuntimeException() {
        // Arrange
        when(bookingRestService.getBookingStatistics(null, null))
            .thenThrow(new RuntimeException("Invalid parameters"));

        // Act
        ResponseEntity<BaseResponseDTO<BookingChartResponseDTO>> response = 
            bookingRestController.getBookingStatistics(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Failed to generate statistics"));
    }

    // ========== CONSTRUCTOR TEST ==========
    
    @Test
    void testConstructor() {
        // Act
        BookingRestController controller = new BookingRestController(bookingRestService);

        // Assert
        assertNotNull(controller);
    }

    // ========== CONSTANTS TESTS ==========
    
    @Test
    void testConstants() {
        // Assert
        assertEquals("/bookings", BookingRestController.BASE_URL);
        assertEquals("/bookings/create", BookingRestController.CREATE_BOOKING);
        assertEquals("/bookings/{id}", BookingRestController.DETAIL_BOOKING);
        assertEquals("/bookings/update/{id}", BookingRestController.UPDATE_BOOKING_FORM);
        assertEquals("/bookings/update", BookingRestController.UPDATE_BOOKING_SUBMIT);
        assertEquals("/bookings/status/pay", BookingRestController.PAY_BOOKING);
        assertEquals("/bookings/status/cancel", BookingRestController.CANCEL_BOOKING);
        assertEquals("/bookings/status/refund", BookingRestController.REFUND_BOOKING);
        assertEquals("/bookings/chart", BookingRestController.CHART_BOOKING);
    }
}
