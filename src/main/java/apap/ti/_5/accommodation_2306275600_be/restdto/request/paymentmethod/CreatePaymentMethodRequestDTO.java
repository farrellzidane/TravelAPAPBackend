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
public class CreatePaymentMethodRequestDTO {
    
    @NotBlank(message = "Method name is required")
    private String methodName;
    
    @NotBlank(message = "Provider is required")
    private String provider;
}
