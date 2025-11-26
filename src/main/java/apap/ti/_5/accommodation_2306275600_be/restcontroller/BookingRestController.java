package apap.ti._5.accommodation_2306275600_be.restcontroller;

import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.CreateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.UpdateBookingRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.request.booking.ChangeBookingStatusRequestDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.BaseResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingResponseDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingUpdateFormDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingListItemDTO;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingDetailResponseDTO;
// import apap.ti._5.accommodation_2306275600_be.restservice.BookingRestService;x
import apap.ti._5.accommodation_2306275600_be.restservice.RBAC.BookingRestServiceRBAC;
import apap.ti._5.accommodation_2306275600_be.restdto.response.booking.BookingChartResponseDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingRestController {
    
    private final BookingRestServiceRBAC bookingRestService;
    
    public BookingRestController(BookingRestServiceRBAC bookingRestService) {
        this.bookingRestService = bookingRestService;
    }
    
    public static final String BASE_URL = "/bookings";
    public static final String CREATE_BOOKING = BASE_URL + "/create";
    public static final String DETAIL_BOOKING = BASE_URL + "/{id}";
    public static final String UPDATE_BOOKING_FORM = BASE_URL + "/update/{id}";
    public static final String UPDATE_BOOKING_SUBMIT = BASE_URL + "/update";
    public static final String PAY_BOOKING = BASE_URL + "/status/pay";
    public static final String CANCEL_BOOKING = BASE_URL + "/status/cancel";
    public static final String REFUND_BOOKING = BASE_URL + "/status/refund";
    public static final String CHART_BOOKING = BASE_URL + "/chart";

    @GetMapping(BASE_URL)
    public ResponseEntity<BaseResponseDTO<List<BookingListItemDTO>>> getAllBookings(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String search) {
        
        var baseResponseDTO = new BaseResponseDTO<List<BookingListItemDTO>>();
        
        try {
            List<BookingListItemDTO> bookings = bookingRestService.getAllBookings(status, search);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(bookings);
            baseResponseDTO.setMessage("Successfully retrieved " + bookings.size() + " booking(s)");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve bookings. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(DETAIL_BOOKING)
    public ResponseEntity<BaseResponseDTO<BookingDetailResponseDTO>> getBookingDetail(
            @PathVariable("id") String bookingID) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingDetailResponseDTO>();
        
        try {
            BookingDetailResponseDTO booking = bookingRestService.getBookingDetail(bookingID);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(booking);
            baseResponseDTO.setMessage("Successfully retrieved booking detail");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setMessage("Booking not found. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve booking detail. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(CREATE_BOOKING)
    public ResponseEntity<BaseResponseDTO<BookingResponseDTO>> createBooking(
            @Valid @RequestBody CreateBookingRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingResponseDTO>();
        
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        try {
            BookingResponseDTO booking = bookingRestService.createBooking(dto);
            
            baseResponseDTO.setStatus(HttpStatus.CREATED.value());
            baseResponseDTO.setData(booking);
            baseResponseDTO.setMessage("Konfirmasi: Booking berhasil dibuat dengan ID " + booking.getBookingID());
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.CREATED);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Konfirmasi: Booking gagal dibuat. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Konfirmasi: Booking gagal dibuat. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(UPDATE_BOOKING_FORM)
    public ResponseEntity<BaseResponseDTO<BookingUpdateFormDTO>> getBookingForUpdate(
            @PathVariable("id") String bookingID) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingUpdateFormDTO>();
        
        try {
            BookingUpdateFormDTO formData = bookingRestService.getBookingForUpdate(bookingID);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(formData);
            baseResponseDTO.setMessage("Successfully retrieved booking for update");
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Cannot update booking. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve booking. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(UPDATE_BOOKING_SUBMIT)
    public ResponseEntity<BaseResponseDTO<BookingResponseDTO>> updateBooking(
            @Valid @RequestBody UpdateBookingRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingResponseDTO>();
        
        // Validate input
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        try {
            BookingResponseDTO booking = bookingRestService.updateBooking(dto);
            
            // Build success message
            String message = "Konfirmasi: Booking berhasil diubah dengan ID " + booking.getBookingID();
            
            // Removed: extraPay and refund handling as features removed
            /*
            // ✅ Safe check - handle both null and zero
            int extraPay = booking.getExtraPay();
            int refund = booking.getRefund();
            
            if (extraPay > 0) {
                message += ". Pembayaran tambahan diperlukan: Rp " + String.format("%,d", extraPay);
            } else if (refund > 0) {
                message += ". Refund tersedia: Rp " + String.format("%,d", refund);
            }
            */
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(booking);
            baseResponseDTO.setMessage(message);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Konfirmasi: Booking gagal diubah. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Konfirmasi: Booking gagal diubah. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(PAY_BOOKING)
    public ResponseEntity<BaseResponseDTO<BookingResponseDTO>> payBooking(
            @Valid @RequestBody ChangeBookingStatusRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingResponseDTO>();
        
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        try {
            BookingResponseDTO booking = bookingRestService.payBooking(dto);
            
            String message = "Konfirmasi: Pembayaran berhasil untuk booking " + booking.getBookingID();
            if (booking.getStatus() == 1) {
                message += ". Status diubah menjadi Payment Confirmed";
            }
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(booking);
            baseResponseDTO.setMessage(message);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Konfirmasi: Pembayaran gagal. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Konfirmasi: Pembayaran gagal. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(CANCEL_BOOKING)
    public ResponseEntity<BaseResponseDTO<BookingResponseDTO>> cancelBooking(
            @Valid @RequestBody ChangeBookingStatusRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingResponseDTO>();
        
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        try {
            BookingResponseDTO booking = bookingRestService.cancelBooking(dto);
            
            String message = "Konfirmasi: Booking " + booking.getBookingID() + 
                           " berhasil dibatalkan. Status diubah menjadi Cancelled";
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(booking);
            baseResponseDTO.setMessage(message);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Konfirmasi: Pembatalan gagal. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Konfirmasi: Pembatalan gagal. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(REFUND_BOOKING)
    public ResponseEntity<BaseResponseDTO<BookingResponseDTO>> refundBooking(
            @Valid @RequestBody ChangeBookingStatusRequestDTO dto,
            BindingResult bindingResult) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingResponseDTO>();
        
        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }
            
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages.toString());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }
        
        try {
            BookingResponseDTO booking = bookingRestService.refundBooking(dto);
            
            String message = "Konfirmasi: Refund berhasil diproses untuk booking " + 
                           booking.getBookingID();
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(booking);
            baseResponseDTO.setMessage(message);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Konfirmasi: Refund gagal. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Konfirmasi: Refund gagal. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(CHART_BOOKING)
    public ResponseEntity<BaseResponseDTO<BookingChartResponseDTO>> getBookingStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        
        var baseResponseDTO = new BaseResponseDTO<BookingChartResponseDTO>();
        
        try {
            BookingChartResponseDTO statistics = bookingRestService.getBookingStatistics(month, year);
            
            String message = String.format("Successfully retrieved booking statistics for %s", 
                                        statistics.getPeriod());
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(statistics);
            baseResponseDTO.setMessage(message);
            baseResponseDTO.setTimestamp(new Date());
            
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
            
        } catch (RuntimeException ex) {
            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage("Failed to generate statistics. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
            
        } catch (Exception ex) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setMessage("Failed to retrieve statistics. Error: " + ex.getMessage());
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}