package apap.ti._5.accommodation_2306275600_be.restdto.request.bill;

import java.util.UUID;

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
public class CreateBillRequestDTO {

    @NotBlank(message = "API Key cannot be empty")
    private String apiKey;

    @NotNull(message = "Customer ID cannot be empty")
    private UUID customerId;

    @NotBlank(message = "Service Name cannot be empty")
    private String serviceName; // "Accomodation Booking" atau "accomodation_booking"

    @NotBlank(message = "Service Reference ID cannot be empty")
    private String serviceReferenceId; // bookingID

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Long amount;
}
