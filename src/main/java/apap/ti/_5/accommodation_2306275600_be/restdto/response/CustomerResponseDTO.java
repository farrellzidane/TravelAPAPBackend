package apap.ti._5.accommodation_2306275600_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerResponseDTO extends EndUserResponseDTO {
    
    private BigDecimal saldo;
}
