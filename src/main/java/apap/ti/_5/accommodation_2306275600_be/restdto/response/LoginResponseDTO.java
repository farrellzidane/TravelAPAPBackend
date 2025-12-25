package apap.ti._5.accommodation_2306275600_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    
    private String userId;
    private String username;
    private String name;
    private String email;
    private String role;
    private String message;
}
