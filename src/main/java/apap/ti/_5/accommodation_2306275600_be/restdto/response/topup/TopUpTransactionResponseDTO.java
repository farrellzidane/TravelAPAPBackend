package apap.ti._5.accommodation_2306275600_be.restdto.response.topup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopUpTransactionResponseDTO {
    private String transactionId;
    private String endUserId;
    private Long amount;
    private String paymentMethodId;
    private String paymentMethodName;
    private String provider;
    private LocalDateTime date;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
