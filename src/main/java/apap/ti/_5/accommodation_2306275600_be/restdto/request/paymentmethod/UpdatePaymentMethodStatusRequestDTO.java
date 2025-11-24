package apap.ti._5.accommodation_2306275600_be.restdto.request.paymentmethod;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentMethodStatusRequestDTO {
    
    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;
    
    @NotBlank(message = "Status is required")
    private String status; // "Active" or "Inactive"
}
