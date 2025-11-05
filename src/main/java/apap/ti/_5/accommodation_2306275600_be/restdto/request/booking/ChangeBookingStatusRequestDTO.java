package apap.ti._5.accommodation_2306275600_be.restdto.request.booking;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBookingStatusRequestDTO {
    
    @NotBlank(message = "Booking ID is required")
    private String bookingID;
}