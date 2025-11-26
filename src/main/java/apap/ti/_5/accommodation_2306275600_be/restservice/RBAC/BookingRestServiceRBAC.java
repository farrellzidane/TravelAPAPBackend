package apap.ti._5.accommodation_2306275600_be.restservice.RBAC;

import java.util.List;
import java.util.UUID;

import apap.ti._5.accommodation_2306275600_be.exceptions.AccessDeniedException;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
import apap.ti._5.accommodation_2306275600_be.restservice.BookingRestService;

public interface BookingRestServiceRBAC extends BookingRestService {
    @Override
    BookingResponseDTO createBooking(CreateBookingRequestDTO dto) throws AccessDeniedException;

    @Override
    List<BookingListItemDTO> getAllBookings(Integer status, String search) throws AccessDeniedException;

    @Override
    BookingDetailResponseDTO getBookingDetail(UUID bookingID) throws AccessDeniedException;

    @Override
    BookingUpdateFormDTO getBookingForUpdate(UUID bookingID) throws AccessDeniedException;

    @Override
    BookingResponseDTO updateBooking(UpdateBookingRequestDTO dto) throws AccessDeniedException;

    @Override
    BookingResponseDTO payBooking(ChangeBookingStatusRequestDTO dto) throws AccessDeniedException;

    @Override
    BookingResponseDTO cancelBooking(ChangeBookingStatusRequestDTO dto) throws AccessDeniedException;

    @Override
    BookingResponseDTO refundBooking(ChangeBookingStatusRequestDTO dto) throws AccessDeniedException;

    @Override
    BookingChartResponseDTO getBookingStatistics(Integer month, Integer year) throws AccessDeniedException;

    // Commented out: updateBookingStatuses no longer needed with simplified 3-status model
    // @Override
    // void updateBookingStatuses() throws AccessDeniedException;
}

