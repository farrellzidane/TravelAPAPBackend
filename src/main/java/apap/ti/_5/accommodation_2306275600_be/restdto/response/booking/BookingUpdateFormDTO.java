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
public class BookingUpdateFormDTO {
    private UUID bookingID;
    
    // Property & Room Info (for dropdowns)
    private String propertyID;
    private String propertyName;
    private String roomTypeID;
    private String roomTypeName;
    private String roomID;
    private String roomNumber;
    
    // Booking Details (prefilled)
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private UUID customerID;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private boolean isBreakfast;
    private int capacity;
    
    // Current Pricing (for reference)
    private int currentTotalPrice;
    private int currentTotalDays;
}