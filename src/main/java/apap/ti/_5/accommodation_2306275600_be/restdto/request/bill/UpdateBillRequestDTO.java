package apap.ti._5.accommodation_2306275600_be.restdto.request.bill;

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
public class UpdateBillRequestDTO {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Service reference ID is required")
    private String serviceReferenceId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Long amount;

    private String apiKey;
}
