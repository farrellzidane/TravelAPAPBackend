package apap.ti._5.accommodation_2306275600_be.restservice;

import java.util.List;

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
    BookingDetailResponseDTO getBookingDetail(String bookingID);
    BookingUpdateFormDTO getBookingForUpdate(String bookingID);
    BookingResponseDTO updateBooking(UpdateBookingRequestDTO dto);
    BookingResponseDTO payBooking(ChangeBookingStatusRequestDTO dto);
    BookingResponseDTO cancelBooking(ChangeBookingStatusRequestDTO dto);
    BookingResponseDTO refundBooking(ChangeBookingStatusRequestDTO dto);
    BookingChartResponseDTO getBookingStatistics(Integer month, Integer year);
    
    void updateBookingStatuses(); // Auto update statuses
}