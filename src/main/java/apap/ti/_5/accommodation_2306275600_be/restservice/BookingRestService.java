package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;

public interface BookingRestService {
    BookingResponseDTO createBooking(CreateBookingRequestDTO dto);
    List<BookingListItemDTO> getAllBookings(Integer status, String search);
    BookingDetailResponseDTO getBookingDetail(UUID bookingID);
    BookingUpdateFormDTO getBookingForUpdate(UUID bookingID);
    BookingResponseDTO updateBooking(UpdateBookingRequestDTO dto);
    BookingResponseDTO payBooking(ChangeBookingStatusRequestDTO dto);
    BookingResponseDTO cancelBooking(ChangeBookingStatusRequestDTO dto);
    BookingResponseDTO refundBooking(ChangeBookingStatusRequestDTO dto);
    BookingChartResponseDTO getBookingStatistics(Integer month, Integer year);
    
    // Commented out: updateBookingStatuses no longer needed with simplified 3-status model
    // void updateBookingStatuses(); // Auto update statuses
}