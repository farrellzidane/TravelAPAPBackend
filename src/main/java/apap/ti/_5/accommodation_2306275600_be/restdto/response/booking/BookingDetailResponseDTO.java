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
public class BookingDetailResponseDTO {
    // Basic Info
    private UUID bookingID;
    private String propertyName;
    private String roomName;
    private String roomNumber;
    
    // Customer Info
    private UUID customerID;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Booking Details
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private int totalDays;
    private boolean isBreakfast;
    private int capacity;
    
    // Pricing
    private int totalPrice;
    // Removed: refund and extraPay fields as per updated requirements
    // private int extraPay;
    // private int refund;
    
    // Status
    private int status;
    private String statusText;
    private String statusColor; // For UI badge color
    
    // Timestamps
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Action Buttons Flags
    private boolean canPay;
    private boolean canUpdate;
    // Removed: canRefund as refund feature removed per updated requirements
    // private boolean canRefund;
    private boolean canCancel;
}