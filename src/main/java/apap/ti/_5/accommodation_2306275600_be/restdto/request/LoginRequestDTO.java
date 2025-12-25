package apap.ti._5.accommodation_2306275600_be.restdto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    
    @NotBlank(message = "Identifier (email or username) is required")
    private String identifier; // Can be email or username
    
    @NotBlank(message = "Password is required")
    private String password;
}
