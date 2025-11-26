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
public class BookingResponseDTO {
    private String bookingID;
    private String roomID;
    private String roomNumber;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalDays;
    private int totalPrice;
    private int status;
    private UUID customerID;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private boolean isBreakfast;
    // Removed: refund and extraPay fields as per updated requirements
    // @Builder.Default
    // private int refund = 0;   
    // @Builder.Default
    // private int extraPay = 0;
    private int capacity;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}