package apap.ti._5.accommodation_2306275600_be.restdto.response.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListItemDTO {
    private UUID bookingID;
    private String propertyName;
    private String roomNumber;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalPrice;
    private int status;
    private String statusText; // "Waiting for Payment", "Payment Confirmed", "Cancelled"
    // Removed: refund field as per updated requirements
    // private int refund; // Refund amount for cancelled bookings
}