package apap.ti._5.accommodation_2306275600_be.restdto.response.paymentmethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponseDTO {
    private String paymentMethodId;
    private String methodName;
    private String provider;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
