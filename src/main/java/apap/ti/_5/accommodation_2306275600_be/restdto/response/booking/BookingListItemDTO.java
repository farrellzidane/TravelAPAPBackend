package apap.ti._5.accommodation_2306275600_be.restdto.response.booking;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListItemDTO {
    private String bookingID;
    private String propertyName;
    private String roomNumber;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalPrice;
    private int status;
    private String statusText; // "Pending", "Confirmed", "Checked-In", "Cancelled", "Completed"
}