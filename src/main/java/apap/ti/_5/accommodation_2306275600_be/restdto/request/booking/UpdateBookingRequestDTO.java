package apap.ti._5.accommodation_2306275600_be.restdto.request.booking;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingRequestDTO {
    
    @NotBlank(message = "Booking ID is required")
    private UUID bookingID;
    
    @NotBlank(message = "Property ID is required")
    private String propertyID;
    
    @NotBlank(message = "Room Type ID is required")
    private String roomTypeID;
    
    @NotBlank(message = "Room ID is required")
    private String roomID;
    
    @NotNull(message = "Check-in date is required")
    private LocalDateTime checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private LocalDateTime checkOutDate;
    
    @NotNull(message = "Customer ID is required")
    private UUID customerID;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    @NotNull(message = "Breakfast option is required")
    private Boolean isBreakfast;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}