package apap.ti._5.accommodation_2306275600_be.restdto.request.bill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayBillRequestDTO {
    private String couponCode;
}
