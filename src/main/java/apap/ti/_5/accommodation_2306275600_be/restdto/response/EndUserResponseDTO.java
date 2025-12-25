package apap.ti._5.accommodation_2306275600_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndUserResponseDTO {
    
    private String id;
    private String username;
    private String name;
    private String email;
    private String role;
    private String gender;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
