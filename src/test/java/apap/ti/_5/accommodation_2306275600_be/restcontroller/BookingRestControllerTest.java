package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.BookingRestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingRestController.class)
class BookingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingRestService bookingRestService;

    @Test
    void testGetAllBookings_Success() throws Exception {
        BookingListItemDTO booking = new BookingListItemDTO();
        booking.setBookingID("BOOK-001");
        booking.setPropertyName("Test Hotel");
        booking.setStatus(1);
        
        List<BookingListItemDTO> bookings = Arrays.asList(booking);
        
        when(bookingRestService.getAllBookings(any(), any())).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].bookingID").value("BOOK-001"));
    }

    @Test
    void testGetAllBookings_WithFilters() throws Exception {
        List<BookingListItemDTO> bookings = Arrays.asList();
        when(bookingRestService.getAllBookings(anyInt(), anyString())).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings")
                .param("status", "1")
                .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetBookingDetail_Success() throws Exception {
        BookingDetailResponseDTO booking = new BookingDetailResponseDTO();
        booking.setBookingID("BOOK-001");
        booking.setCustomerName("John Doe");
        
        when(bookingRestService.getBookingDetail("BOOK-001")).thenReturn(booking);

        mockMvc.perform(get("/api/bookings/BOOK-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.bookingID").value("BOOK-001"));
    }

    @Test
    void testGetBookingDetail_NotFound() throws Exception {
        when(bookingRestService.getBookingDetail("INVALID")).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/api/bookings/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setCustomerID(UUID.randomUUID());
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setCustomerPhone("081234567890");
        request.setRoomID("ROOM-001");
        request.setCheckInDate(LocalDateTime.now().plusDays(1));
        request.setCheckOutDate(LocalDateTime.now().plusDays(3));
        request.setIsBreakfast(true);
        request.setCapacity(2);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setCustomerName("John Doe");
        
        when(bookingRestService.createBooking(any())).thenReturn(response);

        mockMvc.perform(post("/api/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.bookingID").value("BOOK-001"));
    }

    @Test
    void testPayBooking_Success() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        request.setBookingID("BOOK-001");

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setStatus(1);
        
        when(bookingRestService.payBooking(any())).thenReturn(response);

        mockMvc.perform(post("/api/bookings/status/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.bookingID").value("BOOK-001"));
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        request.setBookingID("BOOK-001");

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setStatus(3);
        
        when(bookingRestService.cancelBooking(any())).thenReturn(response);

        mockMvc.perform(post("/api/bookings/status/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetChart_Success() throws Exception {
        BookingChartResponseDTO chart = new BookingChartResponseDTO();
        when(bookingRestService.getBookingStatistics(any(), any())).thenReturn(chart);

        mockMvc.perform(get("/api/bookings/chart")
                .param("month", "11")
                .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void testGetChart_Error() throws Exception {
        when(bookingRestService.getBookingStatistics(any(), any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/bookings/chart")
                .param("month", "11")
                .param("year", "2025"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetBookingForUpdate_Success() throws Exception {
        BookingUpdateFormDTO formData = new BookingUpdateFormDTO();
        formData.setBookingID("BOOK-001");
        
        when(bookingRestService.getBookingForUpdate("BOOK-001")).thenReturn(formData);

        mockMvc.perform(get("/api/bookings/update/BOOK-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.bookingID").value("BOOK-001"));
    }

    @Test
    void testGetBookingForUpdate_Error() throws Exception {
        when(bookingRestService.getBookingForUpdate("BOOK-001")).thenThrow(new RuntimeException("Cannot update"));

        mockMvc.perform(get("/api/bookings/update/BOOK-001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUpdateBooking_Success() throws Exception {
        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO();
        request.setBookingID("BOOK-001");
        request.setPropertyID("PROP-001");
        request.setRoomTypeID("RT-001");
        request.setRoomID("ROOM-001");
        request.setCustomerID(UUID.randomUUID());
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setCustomerPhone("081234567890");
        request.setCheckInDate(LocalDateTime.now().plusDays(2));
        request.setCheckOutDate(LocalDateTime.now().plusDays(4));
        request.setIsBreakfast(true);
        request.setCapacity(2);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setExtraPay(0);
        response.setRefund(0);
        
        when(bookingRestService.updateBooking(any())).thenReturn(response);

        mockMvc.perform(put("/api/bookings/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.bookingID").value("BOOK-001"));
    }

    @Test
    void testUpdateBooking_WithExtraPay() throws Exception {
        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO();
        request.setBookingID("BOOK-001");
        request.setPropertyID("PROP-001");
        request.setRoomTypeID("RT-001");
        request.setRoomID("ROOM-001");
        request.setCustomerID(UUID.randomUUID());
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setCustomerPhone("081234567890");
        request.setCheckInDate(LocalDateTime.now().plusDays(2));
        request.setCheckOutDate(LocalDateTime.now().plusDays(5));
        request.setIsBreakfast(true);
        request.setCapacity(3);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setExtraPay(100000);
        response.setRefund(0);
        
        when(bookingRestService.updateBooking(any())).thenReturn(response);

        mockMvc.perform(put("/api/bookings/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Pembayaran tambahan")));
    }

    @Test
    void testUpdateBooking_WithRefund() throws Exception {
        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO();
        request.setBookingID("BOOK-001");
        request.setPropertyID("PROP-001");
        request.setRoomTypeID("RT-001");
        request.setRoomID("ROOM-001");
        request.setCustomerID(UUID.randomUUID());
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setCustomerPhone("081234567890");
        request.setCheckInDate(LocalDateTime.now().plusDays(2));
        request.setCheckOutDate(LocalDateTime.now().plusDays(3));
        request.setIsBreakfast(false);
        request.setCapacity(1);

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setExtraPay(0);
        response.setRefund(50000);
        
        when(bookingRestService.updateBooking(any())).thenReturn(response);

        mockMvc.perform(put("/api/bookings/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Refund tersedia")));
    }

    @Test
    void testUpdateBooking_Error() throws Exception {
        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO();
        request.setBookingID("BOOK-001");
        request.setPropertyID("PROP-001");
        request.setRoomTypeID("RT-001");
        request.setRoomID("ROOM-001");
        request.setCustomerID(UUID.randomUUID());
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setCustomerPhone("081234567890");
        request.setCheckInDate(LocalDateTime.now().plusDays(2));
        request.setCheckOutDate(LocalDateTime.now().plusDays(4));
        request.setIsBreakfast(true);
        request.setCapacity(2);

        when(bookingRestService.updateBooking(any())).thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/api/bookings/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testRefundBooking_Success() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        request.setBookingID("BOOK-001");

        BookingResponseDTO response = new BookingResponseDTO();
        response.setBookingID("BOOK-001");
        response.setStatus(4);
        
        when(bookingRestService.refundBooking(any())).thenReturn(response);

        mockMvc.perform(post("/api/bookings/status/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.status").value(4));
    }

    @Test
    void testRefundBooking_Error() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        request.setBookingID("BOOK-001");

        when(bookingRestService.refundBooking(any())).thenThrow(new RuntimeException("Refund failed"));

        mockMvc.perform(post("/api/bookings/status/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateBooking_ValidationError() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        // Missing required fields to trigger validation error

        mockMvc.perform(post("/api/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateBooking_Error() throws Exception {
        CreateBookingRequestDTO request = new CreateBookingRequestDTO();
        request.setCustomerID(UUID.randomUUID());
        request.setCustomerName("John Doe");
        request.setCustomerEmail("john@example.com");
        request.setCustomerPhone("081234567890");
        request.setRoomID("ROOM-001");
        request.setCheckInDate(LocalDateTime.now().plusDays(1));
        request.setCheckOutDate(LocalDateTime.now().plusDays(3));
        request.setIsBreakfast(true);
        request.setCapacity(2);

        when(bookingRestService.createBooking(any())).thenThrow(new RuntimeException("Room not available"));

        mockMvc.perform(post("/api/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetAllBookings_Error() throws Exception {
        when(bookingRestService.getAllBookings(any(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void testPayBooking_Error() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        request.setBookingID("BOOK-001");

        when(bookingRestService.payBooking(any())).thenThrow(new RuntimeException("Payment failed"));

        mockMvc.perform(post("/api/bookings/status/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCancelBooking_Error() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        request.setBookingID("BOOK-001");

        when(bookingRestService.cancelBooking(any())).thenThrow(new RuntimeException("Cancel failed"));

        mockMvc.perform(post("/api/bookings/status/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testPayBooking_ValidationError() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        // Missing bookingID to trigger validation error

        mockMvc.perform(post("/api/bookings/status/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCancelBooking_ValidationError() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        // Missing bookingID

        mockMvc.perform(post("/api/bookings/status/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testRefundBooking_ValidationError() throws Exception {
        ChangeBookingStatusRequestDTO request = new ChangeBookingStatusRequestDTO();
        // Missing bookingID

        mockMvc.perform(post("/api/bookings/status/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUpdateBooking_ValidationError() throws Exception {
        UpdateBookingRequestDTO request = new UpdateBookingRequestDTO();
        // Missing required fields

        mockMvc.perform(put("/api/bookings/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}

