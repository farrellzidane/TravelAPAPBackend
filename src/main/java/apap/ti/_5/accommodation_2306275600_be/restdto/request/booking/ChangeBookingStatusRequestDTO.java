package apap.ti._5.accommodation_2306275600_be.restdto.request.booking;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBookingStatusRequestDTO {
    
    @NotNull(message = "Booking ID is required")
    private UUID bookingID;
}